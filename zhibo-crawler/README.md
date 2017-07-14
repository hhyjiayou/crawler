###说明：
次项目用于直播评论后台

###前后端技术说明
前端：Angularjs + Bootstrap + Grunt

###目录结构
前端源代码：src/main/webapp/fe_src
前端发布代码：src/main/webapp/crawler/views (页面模版)&& src/main/webapp/crawler/static (静态资源)

###调试
进入src/main/webapp/fe_src，执行 grunt server -f 即可进行本地调试

###发布
进入src/main/webapp，执行 grunt release -f 即可将代码发布到 src/main/webapp/crawler

###测试环境
url：http://staging.act.zb.mi.com/crawler/add/index
测试服务器：zc-vm-g-live-staging07.bj （/home/work/webapps/zhibo-crawler）

###WIKI
[相关wiki](http://wiki.n.miui.com/pages/viewpage.action?pageId=25066799)