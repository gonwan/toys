// Tested with boost 1.76
// ~11w/s, not so fast, 4 & 24 io loops give almost same performance, almost full 24c cpu ultilize.
#define BOOST_DYN_LINK
#include <boost/beast/core.hpp>
#include <boost/beast/http.hpp>
#include <boost/beast/version.hpp>
#include <boost/asio/dispatch.hpp>
#include <boost/asio/strand.hpp>
#include <functional>
#include <iostream>
#include <memory>
#include <string>
#include <thread>
#include <vector>

namespace beast = boost::beast;         // from <boost/beast.hpp>
namespace http = beast::http;           // from <boost/beast/http.hpp>
namespace net = boost::asio;            // from <boost/asio.hpp>
using tcp = boost::asio::ip::tcp;       // from <boost/asio/ip/tcp.hpp>

// This function produces an HTTP response for the given
// request. The type of the response object depends on the
// contents of the request, so the interface requires the
// caller to pass a generic lambda for receiving the response.
template<
    class Body, class Allocator,
    class Send>
void handle_request(
    http::request<Body, http::basic_fields<Allocator>>&& req,
    Send&& send)
{
    auto const send_content = [&req](http::status status, const std::string& content_type, const std::string& content)
    {
        http::response<http::string_body> res{ status, req.version() };
        //res.set(http::field::server, BOOST_BEAST_VERSION_STRING);
        //res.keep_alive(req.keep_alive());
        res.set(http::field::content_type, content_type);
        res.body() = content;
        res.prepare_payload();
        return res;
    };

    if (req.method() != http::verb::get) {
        return send(send_content(http::status::bad_request, "text/html", "Unknown HTTP-method"));
    }
    if (req.target() == "/json") {
        return send(send_content(http::status::ok, "application/json", "{\"Message\":\"Hello World!\"}"));
    } else {
        return send(send_content(http::status::ok, "text/plain", "Hello World!"));
    }
}

// Report a failure
void fail(beast::error_code ec, char const* what)
{
    std::cerr << what << ": " << ec.message() << std::endl;
}

// Handles an HTTP server connection
class session : public std::enable_shared_from_this<session>
{
    struct send_lambda
    {
        session& self_;

        explicit send_lambda(session& self)
            : self_(self)
        {
        }

        template<bool isRequest, class Body, class Fields>
        void operator()(http::message<isRequest, Body, Fields>&& msg) const
        {
            auto sp = std::make_shared<http::message<isRequest, Body, Fields>>(std::move(msg));
            self_.res_ = sp;
            http::async_write(
                self_.stream_,
                *sp,
                beast::bind_front_handler(&session::on_write, self_.shared_from_this(), sp->need_eof()));
        }
    };

    beast::tcp_stream stream_;
    beast::flat_buffer buffer_;
    http::request<http::string_body> req_;
    std::shared_ptr<void> res_;
    send_lambda lambda_;

public:
    session(
        tcp::socket&& socket)
        : stream_(std::move(socket))
        , lambda_(*this)
    {
    }

    void run()
    {
        net::dispatch(stream_.get_executor(), 
            beast::bind_front_handler(&session::do_read, shared_from_this()));
    }

    void do_read()
    {
        req_ = {};
        stream_.expires_after(std::chrono::seconds(30));
        http::async_read(stream_, buffer_, req_,
            beast::bind_front_handler(&session::on_read, shared_from_this()));
    }

    void on_read(beast::error_code ec, std::size_t bytes_transferred)
    {
        boost::ignore_unused(bytes_transferred);
        if (ec == http::error::end_of_stream) {
            return do_close();
        }
        if (ec) {
            return fail(ec, "read");
        }
        handle_request(std::move(req_), lambda_);
    }

    void on_write(bool close, beast::error_code ec, std::size_t bytes_transferred)
    {
        boost::ignore_unused(bytes_transferred);
        if (ec) {
            return fail(ec, "write");
        }
        if (close) {
            return do_close();
        }
        res_ = nullptr;
        do_read();
    }

    void do_close()
    {
        beast::error_code ec;
        stream_.socket().shutdown(tcp::socket::shutdown_send, ec);
    }

};

// Accepts incoming connections and launches the sessions
class listener : public std::enable_shared_from_this<listener>
{
    net::io_context& ioc_;
    tcp::acceptor acceptor_;

public:
    listener(net::io_context& ioc, tcp::endpoint endpoint)
        : ioc_(ioc)
        , acceptor_(net::make_strand(ioc)) {
        beast::error_code ec;
        acceptor_.open(endpoint.protocol(), ec);
        if (ec) {
            fail(ec, "open");
            return;
        }
        acceptor_.set_option(net::socket_base::reuse_address(true), ec);
        if (ec) {
            fail(ec, "set_option");
            return;
        }
        acceptor_.bind(endpoint, ec);
        if (ec) {
            fail(ec, "bind");
            return;
        }
        acceptor_.listen(net::socket_base::max_listen_connections, ec);
        if (ec) {
            fail(ec, "listen");
            return;
        }
    }

    void run()
    {
        do_accept();
    }

private:
    void do_accept()
    {
        acceptor_.async_accept(
            net::make_strand(ioc_),
            beast::bind_front_handler(&listener::on_accept, shared_from_this()));
    }

    void on_accept(beast::error_code ec, tcp::socket socket)
    {
        if (ec) {
            fail(ec, "accept");
            return;
        } else {
            std::make_shared<session>(std::move(socket))->run();
        }
        do_accept();
    }

};

int main(int argc, char *argv[])
{
    auto const address = net::ip::make_address("0.0.0.0");
    auto const port = 8099;
    auto const threads = (argc == 1) ? 4 : std::max<int>(4, std::atoi(argv[1]));

    net::io_context ioc{ threads };
    std::make_shared<listener>(ioc, tcp::endpoint{ address, port })->run();
    std::cout << "Starting boost server at " << port << std::endl;
    std::vector<std::thread> v;
    v.reserve(threads - 1);
    for (auto i = threads - 1; i > 0; --i) {
        v.emplace_back(
            [&ioc] { ioc.run(); }
        );
    }
    ioc.run();
    return 0;
}
