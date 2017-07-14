'use strict';

/**
 * @ngdoc overview
 * @name ResourceManageApp
 * @description
 * # ResourceManageApp
 *
 *  资源管理模块.
 */
var ResourceManageApp = angular
  .module('ResourceManageApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'commonModule'
  ]);

/**
 * @desc 定义资源管理模块的路由
 */

ResourceManageApp.config(function ($routeProvider) {
  $routeProvider
    .when('/resource_management/commit', {
        templateUrl:'/crawler/views/resource_man/commit.html',  //staging 或者线上
      //templateUrl: '/views/resource_man/commit.html',  //本地测试
      controller: 'commitController'
    })
    .otherwise({
      redirectTo: '/resource_management/commit'
    });
});

/**
 * @desc 左侧导航控制器
 */
ResourceManageApp.controller('leftNav', ['$scope', 'commitNav', function ($scope, commitNav) {
  $scope.navs = commitNav;
  /**
   * @desc 改变导航选中的样式, 此处使用到了curring
   */
  $scope.changeClass = commitNav.changeClass.bind(this, $scope.navs.nav);//好像有问题，是不是写错了
}]);



/**
 * @desc 评论管理控制器
 */
ResourceManageApp.controller('commitController', ['$scope', 'Util', 'commitNav', 'requestConfig', 'commonData', '$timeout', function ($scope, Util, commitNav, requestConfig, commonData, $timeout) {
  $scope.configs = requestConfig.commit;
  // 爬取评论model
  $scope.pullcommitParams = {
    pages: 1, // 总页数
    crawlerUrl: '',
    netType: 1,
    start: 1,
    limit: 20,
    postBarType: {
      hot: true,
      news: false
    }
  };

  // 加载评论参数
  $scope.loadcommitParams = {
    pages: 1, // 总页数
    feedId: '',
    feedOwnerId: '',
    start: 1,
    limit:10/* 20*/
  };



  // 操作确定组件数据
  $scope.confirmContent = commonData.confirmContent;

  //  alert组件
  $scope.alert = commonData.alert;

  //实时监控爬取数据分页搜索条件的变化
  $scope.$watch('pullcommitParams.start', function () {
    if ($scope.pullcommitParams.crawlerUrl != '') {
      $scope.pullCommit('page');
    }
  });

  //实时监控加载评论数据分页搜索条件的变化
  $scope.$watch('loadcommitParams.start', function () {
    if ($scope.loadcommitParams.feedId != '') {
     // $scope.loadCommit('page');
        $scope.loadcommitData = [];
        $scope.allcheck = false;
        var startIndex =( $scope.loadcommitParams.start-1) * $scope.commitPerPage; //每页开始的下标索引
        var i = startIndex;
        var end = startIndex+$scope.commitPerPage;
        var remainCount = $scope.allComments.length - startIndex;
        if(remainCount<$scope.commitPerPage){//遇到当前页显示的评论数是小于每页规定显示的评论数
            end = startIndex+remainCount;
        }
        for(;i<end;i++){
            var item = $scope.allComments[i];
            item.isChecked = false;
            $scope.loadcommitData.push($scope.allComments[i]);
        }
    }
  });

  //删除的时候我们要监控总评论的变化  评论总数  $scope.allComments
 //显示列表是有变化的
    $scope.$watch('allComments.length', function () {
        if ($scope.loadcommitParams.feedId != '') {
            var total_count = $scope.allComments.length;
            $scope.loadcommitParams.pages = total_count% $scope.commitPerPage ? parseInt(total_count/ $scope.commitPerPage)+1 :parseInt(total_count/ $scope.commitPerPage);//总的页数会变化
            $scope.loadcommitData = [];
            if($scope.loadcommitParams.start> $scope.loadcommitParams.pages){ //最后一页被删除的情况，总页数已经减少了一页，当前页还是在以前的最后一页，所以当前页减去1
                $scope.loadcommitParams.start--;
            }
            var startIndex =( $scope.loadcommitParams.start-1) * $scope.commitPerPage; //每页开始的下标索引
            var i = startIndex;
            var end = startIndex+$scope.commitPerPage;
            var remainCount = $scope.allComments.length - startIndex;
            if(remainCount<$scope.commitPerPage){//遇到当前页显示的评论数是小于每页规定显示的评论数
                end = startIndex+remainCount;
            }
            $scope.allcheck = false; //全选变为false
            for(;i<end;i++){
                var item = $scope.allComments[i];
                item.isChecked = false; //每个checkbox变为false
                $scope.loadcommitData.push($scope.allComments[i]);
            }

        }
    });


    //监控爬取地址的变化
    $scope.$watch('pullcommitParams.crawlerUrl', function () {
        $scope.pullcommitParams.start=1; //当前页变为1
        $scope.commitData = [];  //爬取的评论数组变为空
        $scope.flag = false; //标记变量flag变为false ,第一次真正从后台拿到值是start=0 还是start=1  默认是start=0 取值为false
    });

    //监控选择热门评论
    $scope.$watch('pullcommitParams.postBarType.hot', function () {
        $scope.pullcommitParams.start=1;
        $scope.commitData = [];
        $scope.flag = false;
    });

    //监控选择最新评论
    $scope.$watch('pullcommitParams.postBarType.news', function () {
        $scope.pullcommitParams.start=1;
        $scope.commitData = [];
        $scope.flag = false;
    });


  //定义表头信息
  $scope.commitTable = {
    title: '评论管理',
    thead: [
      {
        name: '栏目序列',
        style: 'td_Fifteen'
      },
      {
        name: '评论内容',
        style: 'td_74'
      },
      {
        name: '用户ID',
        style: 'td_Fifteen'
      },
      {
        name: '操作',
        style: 'td_Fifteen'
      }
    ]
  };
    $scope.commitTableLoad = {
        title: '评论管理',
        thead: [
            {
                name: '栏目序列',
                style: 'td_Fifteen'
            },
            {
                name: '评论内容',
                style: 'td_74'
            },
            {
                name: '用户ID',
                style: 'td_Fifteen'
            },
            {
                name: '操作',
                style: 'td_five'
            },
            {
                name: '删除',
                style: 'delBtn', //删除这个带有全选的按钮
                click:true
            },
            {
                name: '',   //这一列是填的是checkbox
                style: 'td_five'
            }
        ]
    };


    $scope.flag = false; //标记第一次真正从后台拿到值是start=0 还是start=1  默认是start=0 取值为false

    $scope.nextBtn = true;//在点击确认爬取出现的表格下面的分页 默认 下一个的按钮是可用的 ，如果没有数据让他变为不可用

    $scope.allcheck = false;//全选 默认是false

    $scope.toutiaoTip = false;//默认的头条提示是不显示的

    //删除评论点击全选
    $scope.clickAll = function(){
        $scope.allcheck = $scope.allcheck ? false : true;
        if( $scope.allcheck){
            for(var i=0;i<$scope.loadcommitData.length;i++){
                var item = $scope.loadcommitData[i];
                item.isChecked = true;
            }
        }else{
            for(var i=0;i<$scope.loadcommitData.length;i++){
                var item = $scope.loadcommitData[i];
                item.isChecked = false;
            }
        }
    }

  //  爬取评论
  $scope.commitData = [];
  $scope.pullCommit = function (tag) {
    if (!Util.IsURL($scope.pullcommitParams.crawlerUrl)) {
      $scope.handlerAjax('fail', '地址不能为空或格式不正确');
      return;
    }
    var toutiaoReg = /^http:\/\/(www.)*toutiao\.com/;
    if(toutiaoReg.test($scope.pullcommitParams.crawlerUrl)){   //根据url验证是头条爬取的评论添加提示信息 ，因为头条是没有最新和热门评论的，如果是头条新闻，我们可以不去选择评论类型
        $scope.toutiaoTip = true;
    }else{                   //如果不是头条新闻，是有热门评论和最新评论的，必须去选择评论类型
        $scope.toutiaoTip = false;
        if (!$scope.pullcommitParams.postBarType.hot && !$scope.pullcommitParams.postBarType.news) {
            $scope.handlerAjax('fail', '请选择评论类型');
            return;
        }
    }

    $scope.configs.pullcommit.transformRequest = function () {
      $scope.loading = true;
    };
    var postBarType = [1];
    if ($scope.pullcommitParams.postBarType.hot && $scope.pullcommitParams.postBarType.news) {
      postBarType = [1, 2];
    }
    if (!$scope.pullcommitParams.postBarType.hot && $scope.pullcommitParams.postBarType.news) {
      postBarType = [2];
    }
      if ($scope.pullcommitParams.postBarType.hot &&! $scope.pullcommitParams.postBarType.news) {
          postBarType = [1];
      }

    $scope.configs.pullcommit.transformRequest = loading_fn;
      var start_param = $scope.pullcommitParams.start-1;
      if($scope.flag ){
          start_param = $scope.pullcommitParams.start;
      }
    $scope.configs.pullcommit.params = {
      crawlerUrl: encodeURI($scope.pullcommitParams.crawlerUrl),
      limit: 20,
      netType: $scope.pullcommitParams.netType,
      start:start_param,
      postBarType:postBarType
    };
    Util.query($scope.configs.pullcommit)
        .then(function (response) {
          var data = response.data;
          if (data.code == 0) {
            $scope.loading = false;
              $scope.nextBtn = true;
             if(!((data.data.hot&&data.data.hot.postbar_data)|| (data.data.new && data.data.new.postbar_data))){
                 $scope.handlerAjax('success', '没有更多数据了');
                 $scope.nextBtn = false;
                 return;
             }

            if(((data.data.new  && data.data.new.postbar_data.length==0)||!data.data.new)&&((data.data.hot && data.data.hot.postbar_data.length==0)||!data.data.hot)){
                $scope.handlerAjax('success', '没有更多数据了');
                $scope.nextBtn = false;
                return;
            }

            $scope.commitData = [];
            $scope.handlerAjax('success', !!tag ? '拉取数据成功' : '爬取成功');
            if (data.data.new && data.data.new.postbar_data && data.data.new.postbar_data.length) {
              data.data.new.postbar_data.forEach(function (item, index) {
               //$scope.commitData.push(item);
                $scope.commitData.push({
                    content:item.content,
                    uid:item.uid,
                    checked:false
                });
              });
              $scope.pullcommitParams.pages = Math.ceil(data.data.new.total_count / 20);
            }
            if (data.data.hot && data.data.hot.postbar_data && data.data.hot.postbar_data.length) {
              data.data.hot.postbar_data.forEach(function (item, index) {
               //$scope.commitData.push(item);
                  $scope.commitData.push({
                      content:item.content,
                      uid:item.uid,
                      checked:false
                  });
              });
              $scope.pullcommitParams.pages += Math.ceil(data.data.hot.total_count / 20);
            }
              if(data.start!=$scope.configs.pullcommit.params.start){
                  $scope.flag = true;
              }
          } else {
            $scope.handlerAjax('fail', data.msg);
          }
        }, function (err) {
          $scope.handlerAjax('fail', err);
        });
  };
  // 加载评论
  $scope.allComments = [];//每次请求时得到的总评论
  $scope.commitPerPage=10;//加载评论列表，每页显示的评论数
  $scope.loadCommit = function (tag) {
      if (!$scope.loadcommitParams.feedId) {
          $scope.handlerAjax('fail', '请填写feedId');
          return;
      }
      var feedIdReg =  /\_\d+$/;
      if(!feedIdReg.test($scope.loadcommitParams.feedId)){
          $scope.handlerAjax('fail', '请填写以下划线加数字结尾正确格式的feedId');
          return;
      }
      $scope.configs.loadFeedComments.transformRequest = loading_fn();
      $scope.configs.loadFeedComments.params = {
          feedId: $scope.loadcommitParams.feedId,
          start:0,   //start=0  limit=500 是传的一个固定的参数 （其实是没有什么作用）最多加载500
          limit:500
      }
      Util.query($scope.configs.loadFeedComments)
          .then(function(response){
              var data = response.data;
              if(data.code==0){
                  $scope.loading = false;
                  $scope.loadcommitParams.start=1;

                  if(!data.total_count){
                      $scope.allComments = [];
                      $scope.handlerAjax('success', '该Feed_ID没有评论可加载');
                      return;
                  }
                  if(!!data.data && data.total_count){
                      $scope.handlerAjax('success', '加载评论成功');
                      $scope.allComments = [];
                      $scope.loadcommitData=[];
                      data.data.forEach(function(item,index){
                          $scope.allComments.push({
                              content: item.content,
                              uid: item.uid,
                              comment_id:item.comment_id,
                              is_good:item.is_good,
                              isChecked:false,
                              index:index
                          });
                      });
                      var total_count = data.total_count;
                      var pages = total_count% $scope.commitPerPage ? parseInt(total_count/ $scope.commitPerPage)+1 :parseInt(total_count/ $scope.commitPerPage);
                      $scope.loadcommitParams.pages = pages;
                      var firstDataCount = total_count<$scope.commitPerPage ? total_count : $scope.commitPerPage;
                      for(var i=0;i<firstDataCount;i++){
                          $scope.loadcommitData.push($scope.allComments[i]);
                      }
                  }
              }else{
                  $scope.handlerAjax('fail',data.msg);
              }
          },function(err){
              $scope.handlerAjax('fail', err);
          })

  };
    //置顶评论
    $scope.top = function(commit){
        if (!$scope.loadcommitParams.feedId || !$scope.loadcommitParams.feedOwnerId) {
            $scope.handlerAjax('fail', '请填写feedId和feedOwnerId');
            return;
        }
        var feedIdReg =  /\_\d+$/;
        if(!feedIdReg.test($scope.loadcommitParams.feedId)){
            $scope.handlerAjax('fail', '请填写以下划线加数字结尾正确格式的feedId');
            return;
        }
        $scope.configs.top.transformRequest = loading_fn();
        $scope.configs.top.params = {
            feedId: $scope.loadcommitParams.feedId,
            feedOwnerId: $scope.loadcommitParams.feedOwnerId,
            commentId:commit.comment_id
        }
        Util.query($scope.configs.top)
            .then(function(response) {
                var data = response.data;
                if(data.code==0){
                    $scope.loading = false;
                    $scope.handlerAjax('success',  '置顶成功');
                    commit.is_good = true;
                }else{
                    $scope.handlerAjax('fail', data.msg);
                }               
            },function(err){
                $scope.handlerAjax('fail', err);
            })
    }
   //评论自动抓取
    $scope.autoPullCommit = function(){
        if (!$scope.loadcommitParams.feedId || !$scope.loadcommitParams.feedOwnerId) {
            $scope.handlerAjax('fail', '请填写feedId和feedOwnerId');
            return;
        }
        var feedIdReg =  /\_\d+$/;
        if(!feedIdReg.test($scope.loadcommitParams.feedId)){
            $scope.handlerAjax('fail', '请填写以下划线加数字结尾正确格式的feedId');
            return;
        }
        if (!Util.IsURL($scope.pullcommitParams.crawlerUrl)) {
            $scope.handlerAjax('fail', '地址不能为空或格式不正确');
            return;
        }
        if (!$scope.pullcommitParams.postBarType.hot && !$scope.pullcommitParams.postBarType.news) {
            $scope.handlerAjax('fail', '请选择评论类型');
            return;
        }
        var postBarType = [1];
        if ($scope.pullcommitParams.postBarType.hot && $scope.pullcommitParams.postBarType.news) {
            postBarType = [1, 2];
        }
        if (!$scope.pullcommitParams.postBarType.hot && $scope.pullcommitParams.postBarType.news) {
            postBarType = [2];
        }
        if ($scope.pullcommitParams.postBarType.hot &&! $scope.pullcommitParams.postBarType.news) {
            postBarType = [1];
        }
        $scope.configs.autoPullCommit.transformRequest = loading_fn();
        $scope.configs.autoPullCommit.params = {
            crawlerUrl: encodeURI($scope.pullcommitParams.crawlerUrl),
            netType: $scope.pullcommitParams.netType,
            postBarType:postBarType,
            feedId: $scope.loadcommitParams.feedId,
            feedOwnerId: $scope.loadcommitParams.feedOwnerId
        }
        Util.query($scope.configs.autoPullCommit)
            .then(function(response){
                var data = response.data;
                if(data.code==0){
                    $scope.loading = false;
                    $scope.handlerAjax('success',  data.msg);
                }
            },function(err){
                $scope.handlerAjax('fail', err);
            })
    }

  // 确定是否添加评论
  $scope.addCommit = function (commit) {
    $scope.commit = commit;
    $scope.confirmContent = {
      confirmShow: true,
      title: '添加评论',
      msg_title: '确认要添加该条评论么？',
      msg: '确认要添加该条评论么？',
      btns: {
        cancel: {
          text: '取消',
          className: 'btn-primary'
        },
        ok: {
          text: '添加',
          className: 'btn-success',
          cb: 'confirmaddcommit'
        }
      }
    };
  };

  // 添加评论
  $scope.loadcommitData=[];
  $scope.confirmaddcommit = function (commit) {
    if (!$scope.loadcommitParams.feedId || !$scope.loadcommitParams.feedOwnerId) {
      $scope.handlerAjax('fail', '请填写feedId和feedOwnerId');
      return;
    }
    var feedIdReg =  /\_\d+$/;
    if(!feedIdReg.test($scope.loadcommitParams.feedId)){
        $scope.handlerAjax('fail', '请填写以下划线加数字结尾正确格式的feedId');
        return;
    }
    $scope.configs.addcommit.transformRequest = loading_fn;
    $scope.configs.addcommit.params = {
      feedId: $scope.loadcommitParams.feedId,
      feedOwnerId: $scope.loadcommitParams.feedOwnerId,
      content: JSON.stringify([{
        content: commit.content,
        uid: commit.uid
      }])
    };
    Util.query($scope.configs.addcommit)
        .then(function (response) {
          var data = response.data;
          if (data.code == 0) {
            $scope.loading = false;
            $scope.handlerAjax('success', '添加成功');
            $scope.confirmContent.confirmShow = false;
          } else {
            $scope.handlerAjax('fail', data.data);
          }
        }, function (err) {
          $scope.handlerAjax('fail', err);
        });
  };

  //删除
  $scope.commentIds=[]; //删除的评论id数组 作为参数
  $scope.indexes=[]; //删除的评论在数组中的下标位置
  $scope.deleteCommit = function () {
    $scope.commentIds=[];
    $scope.indexes=[];
    for(var i=0;i<$scope.loadcommitData.length;i++){
        var item = $scope.loadcommitData[i];
        if(item.isChecked){
            $scope.commentIds.push(item.comment_id);
            $scope.indexes.push(item.index);
        }
    }
    if(!$scope.commentIds.length){
        $scope.handlerAjax('fail', "请选择您要删除的评论");
        return;
    }
    $scope.confirmContent = {
      confirmShow: true,
      title: '删除评论',
      msg_title: '确认要删除该些评论吗？',
      msg: '确认要删除该些评论吗？',
      btns: {
        cancel: {
          text: '取消',
          className: 'btn-primary'
        },
        ok: {
          text: '删除',
          className: 'btn-success',
          cb: 'confirmdelcommit'
        }
      }
    };
  };

  // 删除评论
  $scope.confirmdelcommit = function () {
      if (!$scope.loadcommitParams.feedId) {
          $scope.handlerAjax('fail', '请填写feedId');
          return;
      }
      var feedIdReg =  /\_\d+$/;
      if(!feedIdReg.test($scope.loadcommitParams.feedId)){
          $scope.handlerAjax('fail', '请填写以下划线加数字结尾正确格式的feedId');
          return;
      }
      $scope.configs.delcommit.transformRequest = loading_fn;
      $scope.configs.delcommit.params = {
          feedId: $scope.loadcommitParams.feedId,
          commentIds:$scope.commentIds
      };
      Util.query($scope.configs.delcommit)
          .then(function (response) {
              var data = response.data;
              if (data.code == 0) {
                  $scope.loading = false;
                  $scope.handlerAjax('success', '删除成功');
                  $scope.confirmContent.confirmShow = false;
                  for(var i=0;i<$scope.indexes.length;i++){
                      $scope.allComments.splice($scope.indexes[i],1,'');
                  }
                  var len = $scope.allComments.length;
                  var index = 0;
                  for(var i=0;i<len;i++){
                      var item = $scope.allComments[i];
                      if(item!=''){
                          item.index=index;
                          index++;
                      }
                  }
                  $scope.allComments = $scope.allComments.filter(function(item,index){
                      if(item!=''){
                          return item;
                      }
                  })
              } else {
                  $scope.handlerAjax('fail', data.msg);
              }
          }, function (err) {
              $scope.handlerAjax('fail', err);
          });
  };
  $scope.handlerAjax = Util.showAlert.bind($scope);
  // ajax处理中函数
  $scope.loading = false;
  function loading_fn() {
    $scope.loading = true;
    $scope.handlerAjax('process', '处理中...');
  }
}]);
