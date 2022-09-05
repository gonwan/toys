## CLion Configuration

### Add remote host

- Go to `Setttings` -> `Build, Execution, Deployment` -> `Toolchains`, add a new remote host, fill in SSH credentials.
- Install recent version of gcc/g++/gdb if necessary, and manually set toolchain path.
```shell
# sudo yum install epel-release
# sudo yum install devtoolset-8-gcc devtoolset-8-gcc-c++ devtoolset-8-gdb cmake3
# echo ". /opt/rh/devtoolset-8/enable" | sudo tee -a /etc/profile
```
- Make the newly created toolchain the **default** one, say, move it to the top of the list.
- Go to `Help` -> `Edit Custom Properties`, add a line, then restart with cache reset (`File` -> `Invalidate Caches / Restart`):
```
idea.case.sensitive.fs=true
```
- If header index are not updated, try to delete: `C:\Users\<user>\AppData\Local\JetBrains\CLion2022.2\.remote\<ip>_<port>\<uuid>\`, and reopen the project. 

