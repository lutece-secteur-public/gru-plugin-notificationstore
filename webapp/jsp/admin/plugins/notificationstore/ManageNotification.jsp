<jsp:useBean id="manageNotification" scope="session" class="fr.paris.lutece.plugins.notificationstore.web.NotificationJspBean" />
<% String strContent = manageNotification.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
