## zmq - ZeroMQ snippet

### Features
- Compiles on any C89 compilers
- Demonstrates the publish-subscribe pattern with & without envelopes of ZeroMQ

### Build

- Windows

Works with [vcpkg](https://github.com/microsoft/vcpkg) to simplify build. After bootstrap, run:
```
# vcpkg install zeromq:x64-windows
```
According to the [document](https://github.com/microsoft/vcpkg/blob/master/docs/users/buildsystems/integration.md), invoke cmake with the toolchain file.

- Linux / MacOS: 
```
# sudo yum install zeromq-devel
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

