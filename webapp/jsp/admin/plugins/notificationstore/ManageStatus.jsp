<jsp:useBean id="manageStatus" scope="session" class="fr.paris.lutece.plugins.notificationstore.web.StatusJspBean" />
<% String strContent = manageStatus.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
