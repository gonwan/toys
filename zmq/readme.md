## zmq - ZeroMQ snippet

### Features
- Compiles on any C89 compilers
- Demostrates the publish-subscribe pattern with & without envelopes of ZeroMQ

### Build

- Windows

Works with [vcpkg](https://github.com/Microsoft/vcpkg) to simpify build. After bootstrap, run:
```
# vcpkg install zeromq
```
According to the [document](https://github.com/Microsoft/vcpkg/blob/master/docs/users/integration.md), invoke cmake with the toolchain file.

- Linux / MacOS: 
```
# sudo apt-get install libzmq3-dev
# brew install zmq
# cmake -G "Unix Makefiles"
```

### Output

- Pub-Sub, run from separate terminals:
```
# ./server
# ./client
```

- Pub-Sub with envelopes, run from separate terminals:
```
# ./server key1 key2
# ./client key1
# ./client key2
```
