-- copied from: https://github.com/wg/wrk/blob/master/scripts/setup.lua
-- run with: ./wrk -t 10 -c 500 -d 30s -s test.lua http://localhost:7108

local threads = { }
local tid_counter = 1

function setup(thread)
    thread:set("tid", tid_counter)
    table.insert(threads, thread)
    tid_counter = tid_counter + 1
end

function init(args)
--     local msg = "thread %d created"
--     print(msg:format(tid))
end

function request()
    local id = math.random(0, 99999999)
    local path = "/rocksdb/query?id=" .. id
    return wrk.format(nil, path)
end

function response(status, headers, body)

end

function done(summary, latency, requests)
--     for index, thread in ipairs(threads) do
--         local id  = thread:get("tid")
--         local msg = "thread %d finished"
--         print(msg:format(id))
--     end
end
