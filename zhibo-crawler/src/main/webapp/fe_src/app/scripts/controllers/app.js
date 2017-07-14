/**
 * Created by yiwei on 15/10/21.
 * @desc 公共模块
 */
var commonModule = angular.module('commonModule', [
  'ngResource'
]);

/**
 * @desc 创建导航部分的数据服务
 */
commonModule.factory('navData', function ($location) {
  var host = $location.host();
  if (host == 'mistore.huyu-inc.com' || host == '10.99.184.105') {
    return {
      nav: [
        {
          text: '评论管理',
          isActive: true,
          href: '#/resource_management/commit'
        }
      ]
    }
  } else {
    return {
      nav: [
        {
          text: '评论管理',
          isActive: true,
          href: '#/resource_management/commit'
        }
      ]
    }
  }
});


/**
 * @desc common data
 */
commonModule.factory('commonData', function ($location) {
  var host = $location.host(),
     // eve = 'local';
      env='production';
  if (host == 'mistore.huyu-inc.com' || host == '10.99.184.105') {
    eve = 'dev';
  }
  return {
    loading: false,
    confirmContent: {
        confirmShow: false,
        title: '',
        msg_title: '',
        msg: '',
        btns: {
          cancel: {
            text: '',
            className: ''
          },
          ok: {
            text: '',
            className: ''
          }
        }
    },
    alert: {
      success: true,
      show: false,
      msg: ''
    },
    proessLoading: '<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate processloading"></span><span class="processloading process-msg">请求处理中......</span>'
  }
});

/**
 * @desc 定义资源管理左侧导航数据服务
 */
commonModule.factory('commitNav', function () {
  return {
    nav: [
      {
        type: '管理',
        isActive: false,
        list: [
          {
            text: '评论管理',
            href: '#/resource_management/commit',
            icon: 'glyphicon-cloud',
            isActive: true
          }
        ]
      }
    ],
    changeClass: function (nav, li) {
      nav.forEach(function (item, index) {
        item.isActive = false;
        item.list.forEach(function (_item, index) {
          _item.isActive = false;
          if (li.text == _item.text) {
            item.isActive = true;
          }
        });
      });
      li.isActive = true;
    }
  };
});

/**
 * @desc 定义数据请求配置服务
 */
commonModule.factory('requestConfig', function ($location) {
  var _interface =  {
    local: {
      commit: {
        pullcommit: {
          method: 'GET',
          url: '/testData/pullcommit.json'
        },
        addcommit: {
          method: 'GET',
          url: '/testData/addcommit.json'
        },
        autoPullCommit:{
              method: 'GET',
              url: '/testData/addcommit.json'
        },
          loadFeedComments:{
              method: 'GET',
              url: '/testData/loadfeedcomment.json'
          },
          top:{
              method: 'GET',
              url: '/testData/addcommit.json'
          },
          delcommit: {
              method: 'GET',
              url: '/testData/addcommit.json'
          }
      }
    },
    production: {
      commit: {
        pullcommit: {
          method: 'GET',
          url: '/crawler/api'
        },
        addcommit: {
          method: 'GET',
          url: '/crawler/add_postbar'
        },
        autoPullCommit:{
            method: 'GET',
            url: '/crawler/add_allpostbar'
        },
        loadFeedComments:{
            method: 'GET',
            url: '/crawler/load_feedComments'
        },
        top:{
            method: 'GET',
            url: '/crawler/sticky_feedComments'
        },
        delcommit: {
            method: 'GET',
            url: '/crawler/del_feedComments'
        }
      }
    }
  },
  host = $location.host();
    if(host == "localhost"){
        return _interface.local;
    }else{
        return _interface.production;
    }
});


/**
 * @desc 定义控制器 ----> 控制导航部分
 */
commonModule.controller('navController', ['$scope', 'navData', 'commitNav', function ($scope, navData, commitNav) {
  $scope.navs = navData;
}]);
