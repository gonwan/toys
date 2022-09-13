## CentOS 7 Build
- Enable official SCL repository, install g++-7, and install:
```
yum install libuuid-devel
yum install zlib-devel
yum install openssl-devel
yum install boost169-devel
```
- Install cmake from official `*.tar.gz` file.
- Build install `jsoncpp`:
```
git clone https://github.com/open-source-parsers/jsoncpp
cd jsoncpp/
git checkout 1.9.5
mkdir build
cd build
cmake ..
make && make install
```
- Build install `drogon` framework:
```
git clone https://github.com/drogonframework/drogon
cd drogon
git checkout 1.8.0
git submodule update --init
mkdir build
cd build
cmake .. -DBOOST_INCLUDEDIR=/usr/include/boost169 -DBOOST_LIBRARYDIR=/usr/lib64
make && sudo make install
```
