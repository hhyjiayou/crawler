/**
 * Created by yiwei on 15/10/21.
 * @desc 定义指令
 */

/**
 * @desc 定义一个弹窗指令
 */
commonModule.directive('miDialog', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {content: '=dialogContent'},
    template: '<div class="popover bottom" ng-click="hide()">' +
                '<div class="arrow"></div>' +
                '<h3 class="popover-title" ng-bind="content.title"></h3>' +
                '<div class="popover-content">' +
                  '<table class="table table-striped">' +
                    /*'<thead>' +
                      '<tr><td ng-repeat="head in content.thead" ng-bind="head"></td><tr>' +
                    '</thead>' +*/
                    '<tbody>' +
                      '<tr ng-repeat="app in content.AppList.data">' +
                        '<td ng-bind="($index+1)"></td><td ng-bind="app.app_id"></td><td ng-bind="app.app_name"></td>' +
                      '</tr>' +
                    '</tbody>' +
                  '</table>' +
                '</div>' +
              '</div>',
    link: function (scope, element, attrs) {
      scope.hide = function () {
        //content.showMe = false;
      }
    }
  };
});

/**
 * @desc 分页指令
 */
commonModule.directive('miPaging', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      /*list: '=pageContent',*/
      curPage: '=currentPage',
      pages: '=pageCount',
      showPage: '=currentListpage',
        nextBtn:'=nextBtn'
    },
    template: '<nav class="navpage">' +
                '<ul class="pager">' +
                  '<li><span ng-class="{pointer:curPage>1, pageHover: curPage==1}" ng-click="go(1)">首页</span></li>' +
                  '<li><span ng-class="{pointer:curPage>1, pageHover: curPage==1}" ng-click="prev()" ><span aria-hidden="true">&larr;</span></span></li>' +
                  '<li>' +
                    '<input type="text" class="form-control page-input" ng-model="curPage">' +
                  '</li>' +
                  '<li><span ng-class="{pointer:nextBtn, pageHover: !nextBtn}" ng-click="next()" ng-disabled="!nextBtn"><span aria-hidden="true">&rarr;</span></span></li>' +
                '</nav>',
    link: function (scope, element, attrs) {
      scope.go = function (pages) {
        scope.curPage = pages;
        console.log(pages);
      };

      scope.next = function () {
        console.log(scope.curPage);
          if(scope.nextBtn){
              scope.curPage = parseInt(scope.curPage) + 1;
          }

      };

      scope.prev = function () {
        if (scope.curPage > 1) {
            console.log(scope.curPage);
          scope.curPage = parseInt(scope.curPage) - 1;
        }
      };
    }
  };
});
commonModule.directive('miPagingcommit', function () {
    return {
        restrict: 'EA',
        replace: true,
        transclude: true,
        scope: {
            /*list: '=pageContent',*/
            curPage: '=currentPage',
            pages: '=pageCount',
            showPage: '=currentListpage'
        },
        template: '<nav class="navpage">' +
            '<ul class="pager">' +
            '<li><span ng-class="{pointer:curPage>1, pageHover: curPage==1}" ng-click="go(1)">首页</span></li>' +
            '<li><span ng-class="{pointer:curPage>1, pageHover: curPage==1}" ng-click="prev()" ><span aria-hidden="true">&larr;</span></span></li>' +
            '<li>' +
                '<input type="text" class="form-control page-input" ng-model="curPage">' +
                '<span class="page-msg">of</span>' +
                '<span class="page-msg" ng-bind="pages"></span>' +
            '</li>' +
            '<li><span ng-class="{pointer:curPage!=pages, pageHover: curPage==pages}" ng-click="next()"><span aria-hidden="true">&rarr;</span></span></li>' +
            '<li><span ng-class="{pointer:curPage<pages, pageHover: curPage==pages}" ng-click="go(pages)">尾页</span></li>' +
            '</nav>',
        link: function (scope, element, attrs) {
            scope.go = function (pages) {
                scope.curPage = pages;
                console.log(pages);
            };

            scope.next = function () {
                console.log(scope.curPage);
                if(scope.curPage<scope.pages){
                    scope.curPage = parseInt(scope.curPage) + 1;
                }

            };

            scope.prev = function () {
                if (scope.curPage > 1) {
                    console.log(scope.curPage);
                    scope.curPage = parseInt(scope.curPage) - 1;
                }
            };
        }
    };
});
/**
 * @desc tooltip指令
 */
commonModule.directive('miTooltip', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      pc: '=tipsStyle',
      content: '=tipsContent',
      class_name: '=className'
    },
    template: '<div ng-class="{tooltip: true,  right: !class_name, tipsRight: !class_name, bottom: class_name, tipsBottom: class_name}" role="tooltip">' +
                '<div class="tooltip-arrow"></div>' +
                '<div class="tooltip-inner" ng-bind="content"></div>' +
              '</div>',
    link: function (scope, element, attrs) {

    }
  };
});

/**
 * @desc 定义一个表头指令
 */
commonModule.directive('miTablehead', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      heads: '=headContent',
      delCommit:'&',
      allcheck:'=allCheck',
      clickAll:'&'
    },
    template: '<div class="table-head">' +
                '<table class="table table-striped">' +
                  '<thead><tr>'+
                       '<td ng-repeat="head in heads" ng-bind="head.name" class="{{head.style}}" ng-if="!head.click&&head.name"></td>'+
                       '<td ng-repeat="head in heads" ng-bind="head.name" class="{{head.style}}" ng-if="head.click" ng-click="del()"></td>'+
                       '<td ng-repeat="head in heads"  class="{{head.style}}" ng-if="!head.name"><input type="checkbox" ng-click="clickall()" ng-checked="allcheck"/>全选</td>'+
                   '</tr></thead>' +
                '</table>' +
              '</div>',
    link: function (scope, element, attrs) {
       scope.del = function(){
           scope.delCommit();
       }
        scope.clickall = function(){
            scope.clickAll();
        }
    }
  }
});


/**
 * @desc 定义一个弹窗指令
 */
commonModule.directive('miPopups', ['$window', function ($window) {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      title: '=headTitle',
      msg: '=msgContent',
      show: '=popupShow',
      url: '=determineUrl'
    },
    template: '<div class="popups-warp">' +
                '<div class="popups">' +
                  '<h3 ng-bind="title"></h3>' +
                  '<p ng-bind-html="msg|to_html"></p>' +
                  '<div class="ok_btn" ng-click="determine()">确定</div>' +
                '</div>' +
              '</div>',
    link: function (scope, element, attrs) {
      scope.determine = function () {
        scope.show = false;
        if (scope.url != '') {
          $window.location.href = scope.url + '/';
        }
      };
    }
  }
}]);

/**
 * @desc 定义一个tab指令
 */
commonModule.directive('miTab', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      tab_title: '=tabTitle',
      tab_show: '=tabShow'
    },
    template: '<ul class="nav nav-tabs">' +
                '<li role="presentation" ng-repeat="title in tab_title" ng-class="{active: $index ==0, tab_notactive: $index != 0}"><a href="" ng-click="toggleTab(title.show, title.hide, $index)">{{title.title}}</a></li>' +
              '</ul>',
    link: function (scope, element, attrs) {
      scope.toggleTab = function (show, hide, index) {
        scope.tab_show[show] = true;
        scope.tab_show[hide] = false;
        $('li[role="presentation"]').removeClass('active').addClass('tab_notactive');
        $('li[role="presentation"]').eq(index).addClass('active').removeClass('tab_notactive');
      };
    }
  };
});

/**
 * @desc 定义一个支持排序的表头指令
 */
commonModule.directive('miSortTablehead', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      heads: '=headContent',
      curpages: '=headCurpage',
      list: '=headList'
    },
    template: '<div class="table-head">' +
                '<table class="table table-striped sorttable">' +
                  '<thead><tr>' +
                    '<td ng-repeat="head in heads" class="{{head.style}}">' +
                      '<span ng-bind="head.name" class="td_name"></span>' +
                      '<span ng-if="head.sort" class="sort-icon">' +
                        '<span class="glyphicon glyphicon-triangle-top pointer" aria-hidden="true" ng-click="sortByAsc(head.sort.property)"></span>' +
                        '<span class="glyphicon glyphicon-triangle-bottom pointer" aria-hidden="true" ng-click="sortByDesc(head.sort.property)"></span>' +
                      '</span>' +
                    '</td>' +
                  '</tr></thead>' +
                '</table>' +
              '</div>',
    link: function (scope, element, attrs) {
      // 降序
      scope.sortByDesc = function (field) {
        scope.list[scope.curpages-1].sort(function (a, b) {
          var temp_a = 0,
              temp_b = 0;
          if (field == 'create_time') {
            temp_a = new Date(a[field]);
            temp_b = new Date(b[field]);
            return temp_b.getTime() - temp_a.getTime();
          }

          if (field == "app_id") {
            temp_a = a[field];
            temp_b = b[field];
            return temp_b - temp_a;
          }
        });
      };

      //升序
      scope.sortByAsc = function (field) {
        scope.list[scope.curpages-1].sort(function (a, b) {
          var temp_a = 0,
              temp_b = 0;
          if (field == 'create_time') {
            temp_a = new Date(a[field]);
            temp_b = new Date(b[field]);
            return  temp_a.getTime() - temp_b.getTime();
          }

          if (field == "app_id") {
            temp_a = a[field];
            temp_b = b[field];
            return temp_a - temp_b;
          }
        });
      };
    }
  }
});

/**
 * @desc 定义一个表单验证指令
 */
commonModule.directive('miFormValidity', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      msg: '=msgObj',
      content: 'validityContent'
    },
    template: '<span class="tipsmsg {{msg.font}}" ng-bind="msg.msg"></span>',
    link: function (scope, element, attrs) {
      scope.validity = function () {
        var patterns = scope.msg.pattern.split(' ');
      };
    }
  };
});

/**
 * @desc 定义一个loading指令
 */
commonModule.directive('miLoading', function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {

    },
    template: '<div class="load-warp">' +
                '<div class="loading">' +
                  '<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span>' +
                  '<span>数据加载中......</span>' +
                '</div>' +
              '</div>',
    link: function (scope, element, attrs) {

    }
  };
});

/**
 * @desc 定义一个数据加载异常指令
 */
commonModule.directive('miExceptionHandler', ['$window', function ($window) {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      noData: '=isNodata',
      exception: '=isException',
      onAction: '&'
    },
    template: '<div>' +
                '<p ng-class="{exceptionMsg: true, getException: exception, noData: noData}">' +
                  '<span ng-if="exception">数据请求异常</span>' +
                  '<span ng-if="noData && !exception">没有符合的数据</span>' +
                 '</p>' +
                '<div class="btn restart-btn exception-btn" ng-if="exception" ng-click="onAction()">' +
                  '<span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>' +
                  '重新加载' +
                '</div>' +
              '</div>',
    link: function (scope, element, attrs) {
      /*scope.refresh = function (action) {
        $window.location.href = action + '?' + Date.now();
      };*/
    }
  };
}]);

/**
 * @desc 定义一个select指令
 */
commonModule.directive('miSelector', ['$window', function ($window) {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      detail: '=showDetails',
      flag: '=showFlags'
    },
    template: '<div class="form-control userSelector">' +
                  '<div>' +
                    '<h3>信息：</h3>' +
                    '<p><span>range：</span><span ng-bind="detail.range_begin"></span>-<span ng-bind="detail.range_end"></span></p>' +
                    '<p><span>状态：</span><span ng-bind="detail.instance_status"></span></p>' +
                  '</div>' +
                  '<div>' +
                    '<h3>主机器信息：</h3>' +
                    '<p><span>IP：</span><span ng-bind="detail.instance_master.instance_ip"></span></p>' +
                    '<p><span>port：</span><span ng-bind="detail.instance_master.instance_port"></span></p>' +
                    '<p><span>实例大小：</span><span ng-bind="detail.instance_master.instance_size"></span>G</p>' +
                  '</div>' +
                  '<div>' +
                    '<h3>从机器信息：</h3>' +
                    '<p><span>IP：</span><span ng-bind="detail.instance_slave.instance_ip"></span></p>' +
                    '<p><span>port：</span><span ng-bind="detail.instance_slave.instance_port"></span></p>' +
                    '<p><span>实例大小：</span><span ng-bind="detail.instance_slave.instance_size"></span>G</p>' +
                  '</div>' +
                  '<div class="btn" ng-click="close()">关闭</div>' +
              '</div>',
    link: function (scope, element, attrs) {
      scope.close = function () {
        scope.flag = false;
      }
    }
  };
}]);

/**
 * @desc 定义一个clipboard指令
 */
commonModule.directive('miClipboard', [function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      content: '=clipboardContent',
      warp: '=warpSelector',
      clip: '=clipSelector'
    },
    template: '<div id="{{warp}}" class="clipboard-warp">' +
                '<span class="btn-clipboard" id="{{clip}}" ng-mouseenter="showTips(true)">复制</span><span class="clipboard-tips"></span>' +
              '</div>',
    link: function (scope, element, attrs) {
      scope.showTips = function (show, msg) {
        scope.tips = $('#' + scope.warp +' .clipboard-tips');
        scope.tips.html('点击复制到剪切板').show();
        scope.copy && scope.copy();
      };

      scope.copy = function () {
        scope[scope.warp] = {
          clip: null
        };
        if (!scope[scope.warp].clip) {
          ZeroClipboard.setMoviePath('/static/images/icons/ZeroClipboard.swf');
          scope[scope.warp].clip = new ZeroClipboard.Client();
          scope[scope.warp].clip.setHandCursor(true);
          scope[scope.warp].clip.addEventListener('load', function (client) {
            scope[scope.warp].clip.setText(scope.content);
          });
          scope[scope.warp].clip.addEventListener('mouseover', function (client) {
            scope.tips.html('点击复制到剪切板').show();
          });
          scope[scope.warp].clip.addEventListener('mouseout', function (client) {
            scope.tips.html('').hide();
          });
          scope[scope.warp].clip.addEventListener('mousedown', function (client) {
            scope[scope.warp].clip.setText(scope.content);
            scope.tips.html('复制成功！').show();
          });
          scope[scope.warp].clip.glue(scope.clip, scope.warp);
        }
      };
    }
  };
}]);

/**
 * @desc 定义一个accordion指令
 */
commonModule.directive('accordion', [function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    template: '<div ng-transclude></div>',
    controller: function () {
      var expanders = [];
      this.gotOpened = function (selectedExpander, index) {
        angular.forEach(expanders, function (expander) {
          if (selectedExpander != expander.scope) {
            expander.scope.showMe = false;
            expander.ele.find('span.glyphicon').removeClass('glyphicon-minus-sign').addClass('glyphicon-plus-sign');
            expander.ele.removeClass('active');
          } else {
            if (expander.ele.find('span.glyphicon').hasClass('glyphicon-plus-sign')) {
              expander.ele.find('span.glyphicon').removeClass('glyphicon-plus-sign').addClass('glyphicon-minus-sign');
            } else {
              expander.ele.find('span.glyphicon').removeClass('glyphicon-minus-sign').addClass('glyphicon-plus-sign');
            }
            expander.ele.addClass('active');
          }
        });
      };

      this.addExpander = function (expander) {
        expanders.push(expander);
      };
    }
  };
}]);

/**
 * @desc 定义一个expander指令
 */
commonModule.directive('expander', [function () {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    require: '^?accordion',
    scope: {
      nav: '=expanderNav',
      index: '=expanderIndex',
      changeClass: '&expanderChangeClass'
    },
    template: '<ul class="list-group left-nav-ul">' +
                  '<li class="left-nav" ng-click="toggle(index)">' +
                    '<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>' +
                    '<span ng-bind="nav.type"></span>' +
                  '</li>' +
                  "<div ng-class='{leftNavChild: true, show: showMe}'>" +
                    '<a ng-repeat="nav_li in nav.list" ng-href="{{nav_li.href}}" class="list-group-item" ng-class="{active: nav_li.isActive}">' +
                      '<span class="glyphicon {{nav_li.icon}}" aria-hidden="true"></span>' +
                      '<span ng-bind="nav_li.text"></span>' +
                    '</a>' +
                  '</div>' +
              '</ul>',
    link: function (scope, element, attrs, accordionController) {
      scope.showMe = false;
      scope.isActive = false;
      accordionController.addExpander({
        scope: scope,
        nav: scope.nav,
        ele: element.find('li.left-nav')
      });
      scope.toggle = function (index) {
        scope.showMe = !scope.showMe;
        accordionController.gotOpened(scope);
      };

      if (scope.index == 0) {
        scope.toggle();
      }
    }
  };
}]);

/**
 * @desc 弹窗指令
 */
commonModule.directive('miConfirm', function ($location) {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      content: '=confirmContent',
      loading: '=confirmLoading',
      onConfirmdelcommit: '&',
      onConfirmaddcommit: '&'
    },
    templateUrl: $location.host() != 'localhost' ?  '/crawler/static/scripts/directives/confirm_tpl.html' : '/scripts/directives/confirm_tpl.html',
    link: function (scope, element, attrs) {
      scope.cancel = function () {
        scope.content.confirmShow = false;
      };

      scope.ok = function (cb) {
        switch (cb) {
          case 'confirmaddcommit':
            scope.onConfirmaddcommit();
            break;
          case 'confirmdelcommit':
            scope.onConfirmdelcommit();
            break;
        }
      };
    }
  };
});

/**
 * @desc alert指令
 */
commonModule.directive('miAlert', function ($location) {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      obj: '=alertObj'
    },
    templateUrl: $location.host() != 'localhost' ?  '/crawler/static/scripts/directives/alert_tpl.html' : '/scripts/directives/alert_tpl.html',
    link: function (scope, element, attrs) {
      scope.hide = function () {
        scope.obj.show = false;
      };
    }
  };
});