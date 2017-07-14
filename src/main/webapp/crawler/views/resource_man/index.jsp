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
    <link rel="stylesheet" href="../static/styles/f38437b0.all.css">
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

    <script src="../static/scripts/lib/b2ab3359.lib.js"></script>

    <script src="../static/scripts/controllers/cb8706ab.app.js"></script>
    <script src="../static/scripts/filters/6aeaf84e.filter.js"></script>
    <script src="../static/scripts/directives/c1dc9fe5.directive.js"></script>
    <script src="../static/scripts/services/add3e247.service.js"></script>
    <script src="../static/scripts/controllers/13a5cc0f.resourceMannager.js"></script>
  </body>
</html>
