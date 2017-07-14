<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!doctype html>
<html ng-app="ResourceManageApp">
  <head>
    <meta charset="utf-8">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="../../static/styles/eb97c7e3.all.css">
  </head>
  <body ng-controller="resourceManageController">
    <!--<ng-include src="'/views/common/nav.jsp'"></ng-include>-->
    <%@ include file="/views/common/nav.jsp"%>
    <div class="container" ng-controller="leftNav">
      <div class="resource_man_container">
        <div class="row">
          <div class="col-lg-4 resource_man_left">
            <ul class="list-group" ng-repeat="nav in navs.nav">
              <li class="left-nav" ng-bind="nav.type"></li>
              <a ng-repeat="nav_li in nav.list" ng-href="{{nav_li.href}}" ng-class="{'list-group-item': true, active: nav_li.isActive}" ng-click="changeClass(nav.list[$index])">
                <span class="glyphicon {{nav_li.icon}}" aria-hidden="true"></span>
                <span ng-bind="nav_li.text"></span>
              </a>
            </ul>
          </div>
          <div class="col-lg-4 resource_man_right">
            <div ng-view=""></div>
          </div>
        </div>
      </div>
    </div>
    <ng-include src="'/views/common/footer.html'"></ng-include>
    <!-- Google Analytics: change UA-XXXXX-X to be your site's ID -->
    <script>
      !function(A,n,g,u,l,a,r){A.GoogleAnalyticsObject=l,A[l]=A[l]||function(){
      (A[l].q=A[l].q||[]).push(arguments)},A[l].l=+new Date,a=n.createElement(g),
      r=n.getElementsByTagName(g)[0],a.src=u,r.parentNode.insertBefore(a,r)
      }(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-XXXXX-X');
      ga('send', 'pageview');
    </script>

    <script src="../../static/scripts/lib/d9593903.lib.js"></script>

    <script src="../../static/scripts/controllers/3755fe36.app.js"></script>
    <script src="../../static/scripts/directives/dc06d571.directive.js"></script>
    <script src="../../static/scripts/services/f27f4362.service.js"></script>
    <script src="../../static/scripts/controllers/d7856893.resourceMannager.js"></script>
  </body>
</html>
