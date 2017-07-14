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
    <!-- build:css ../../static/styles/all.css -->
    <link rel="stylesheet" href="/bower_components/bootstrap/dist/css/bootstrap.css" />
    <link rel="stylesheet" href="/bower_components/angular-loading-bar/build/loading-bar.min.css"/>
    <link rel="stylesheet" href="../../styles/main.css">
    <link rel="stylesheet" href="../../styles/index.css"/>
    <!-- endbuild -->
  </head>
  <body>
    <%@ include file="/crawler/views/common/nav.jsp"%>
    <div class="container" ng-controller="leftNav">
      <div class="resource_man_container">
        <div class="row">
          <div class="col-lg-4 resource_man_left">
            <accordion>
              <expander ng-repeat="nav in navs.nav" expander-nav="nav" expander-index="$index" expander-changeClass="changeClass()"></expander>
            </accordion>
          </div>
          <div class="col-lg-4 resource_man_right">
            <div ng-view=""></div>
          </div>
          <br class="clear"/>
        </div>
      </div>
    </div>
    <<%@ include file="/crawler/views/common/footer.jsp"%>
    <%--<ng-include src="/crawler/views/common/footer.jsp"></ng-include>--%>
    <!-- Google Analytics: change UA-XXXXX-X to be your site's ID -->
    <script>
      !function(A,n,g,u,l,a,r){A.GoogleAnalyticsObject=l,A[l]=A[l]||function(){
          (A[l].q=A[l].q||[]).push(arguments)},A[l].l=+new Date,a=n.createElement(g),
        r=n.getElementsByTagName(g)[0],a.src=u,r.parentNode.insertBefore(a,r)
      }(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-XXXXX-X');
      ga('send', 'pageview');
    </script>

    <!-- build:js ../../static/scripts/lib/lib.js -->
    <script src="/bower_components/jquery/dist/jquery.js"></script>
    <script src="/bower_components/angular/angular.js"></script>
    <script src="/bower_components/bootstrap/dist/js/bootstrap.js"></script>
    <script src="/bower_components/angular-animate/angular-animate.js"></script>
    <script src="/bower_components/angular-cookies/angular-cookies.js"></script>
    <script src="/bower_components/angular-resource/angular-resource.js"></script>
    <script src="/bower_components/angular-route/angular-route.js"></script>
    <script src="/bower_components/angular-sanitize/angular-sanitize.js"></script>
    <script src="/bower_components/angular-touch/angular-touch.js"></script>
    <script src="/bower_components/angular-loading-bar/build/loading-bar.min.js"></script>
    <!-- endbuild -->

    <!-- build:js ../../static/scripts/controllers/app.js -->
    <script src="../../scripts/controllers/app.js"></script>
    <!--endbuild -->
    <!-- build:js ../../static/scripts/filters/filter.js -->
    <script src="../../scripts/filters/filter.js"></script>
    <!--endbuild -->
    <!-- build:js ../../static/scripts/directives/directive.js -->
    <script src="../../scripts/directives/directive.js"></script>
    <!--endbuild -->
    <!-- build:js ../../static/scripts/services/service.js -->
    <script src="../../scripts/services/service.js"></script>
    <!--endbuild -->
    <!-- build:js ../../static/scripts/controllers/resourceMannager.js -->
    <script src="../../scripts/controllers/resourceMannager.js"></script>
    <!--endbuild -->
  </body>
</html>
