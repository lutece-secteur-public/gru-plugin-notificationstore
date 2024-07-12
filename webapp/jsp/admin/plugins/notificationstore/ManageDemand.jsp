<jsp:useBean id="managedemandDemand" scope="session" class="fr.paris.lutece.plugins.notificationstore.web.DemandJspBean" />
<% String strContent = managedemandDemand.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
