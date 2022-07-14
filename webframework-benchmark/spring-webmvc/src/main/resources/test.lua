-- copied from: https://github.com/wg/wrk/blob/master/scripts/setup.lua
-- run with: ./wrk -t 3 -c 30 -d 10s -s test.lua http://localhost:8122

local uuids = {
    "111111",
    "222222",
    "333333"
}

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
    local uuid = uuids[math.random(#uuids)]
    local path = "/luatest?tid=" .. tid .. "&uuid=" .. uuid
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
