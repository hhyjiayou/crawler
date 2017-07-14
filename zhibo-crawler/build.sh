#!/bin/bash -x

# 部署系统会将项目在部署系统中的名字作为第一个参数传递给 build.sh
# 因为 httpprocess 所在的 git 源中包含多个模块,
# 所以我们需要通过这个参数区分不同模块的编译动作
name=$1
cluster=$(echo $name |sed -n 's/.*_cluster\.\([^._]*\)_.*/\1/p')
job=$(echo $name |sed -n 's/^job\.\([^._]*\)_.*/\1/p')
sed -i "s/#XBOX_JOB_NAME#/$job/" pom.xml
rm -rf release

# cluster 的值可能是staging，production-[sd|lg|hh], onebox,具体值在部署时会传过来
mvn -U clean package -D$cluster=true -D$job=true || { exit 1; }
mkdir release 
cp -r deploy target/*.war release/ && \
cd release && jar -xvf *.war && /bin/rm *.war
