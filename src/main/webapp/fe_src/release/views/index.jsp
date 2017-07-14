<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!doctype html>
<html ng-app="commonModule">
  <head>
    <meta charset="utf-8">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="../static/styles/eb97c7e3.all.css">
  </head>
  <body>
    <!--<ng-include src="'/views/common/nav.jsp'"></ng-include>-->
    <%@ include file="/views/common/nav.jsp"%>
    <div class="container">
      <ng-include src="'/views/common/main.html'"></ng-include>
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

    <script src="../static/scripts/lib/d9593903.lib.js"></script>
    <script src="../static/scripts/controllers/3755fe36.app.js"></script>
  </body>
</html>
