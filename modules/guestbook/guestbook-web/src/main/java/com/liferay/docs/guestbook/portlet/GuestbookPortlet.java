package com.liferay.docs.guestbook.portlet;

import com.liferay.docs.guestbook.model.Guestbook;
import com.liferay.docs.guestbook.model.GuestbookEntry;
import com.liferay.docs.guestbook.constants.GuestbookPortletKeys;
import com.liferay.docs.guestbook.service.GuestbookEntryLocalService;
import com.liferay.docs.guestbook.service.GuestbookLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.monitoring.Level;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * @author jeonghyeon
 */
@Component(
	immediate = true,
	property = {
		      "com.liferay.portlet.display-category=category.social",
		      "com.liferay.portlet.instanceable=false",
		      "com.liferay.portlet.scopeable=true",
		      "javax.portlet.display-name=Guestbook",
		      "javax.portlet.name=" + GuestbookPortletKeys.GUESTBOOK,
		      "javax.portlet.expiration-cache=0",
		      "javax.portlet.init-param.template-path=/",
		      "javax.portlet.init-param.view-template=/guestbook/view.jsp",
		      "javax.portlet.resource-bundle=content.Language",
		      "javax.portlet.security-role-ref=power-user,user",
		      "javax.portlet.supports.mime-type=text/html",
	},
	service = Portlet.class
)

public class GuestbookPortlet extends MVCPortlet {

	@Reference
	private GuestbookEntryLocalService _guestbookEntryLocalService;
	
	@Reference
	private GuestbookLocalService _guestbookLocalService;
	
	
	public void addEntry(ActionRequest request, ActionResponse response)
            throws PortalException {

        ServiceContext serviceContext = ServiceContextFactory.getInstance(GuestbookEntry.class.getName(), request);

        String userName = ParamUtil.getString(request, "name");
        String email = ParamUtil.getString(request, "email");
        String message = ParamUtil.getString(request, "message");
        long guestbookId = ParamUtil.getLong(request, "guestbookId");
        long entryId = ParamUtil.getLong(request, "entryId");

	    if (entryId > 0) {
	
	        try {
	
	            _guestbookEntryLocalService.updateGuestbookEntry(
	                serviceContext.getUserId(), guestbookId, entryId, userName,
	                email, message, serviceContext);
	
	            response.setRenderParameter(
	                "guestbookId", Long.toString(guestbookId));
	            System.out.println("AddEntry" + Long.toString(guestbookId));
	            
	        }
	        catch (Exception e) {
	            System.out.println(e);
	            PortalUtil.copyRequestParameters(request, response);
	
	            response.setRenderParameter("mvcPath", "/guestbook/edit_entry.jsp");
	        }
	
	    } else {
	
	        try {
	            _guestbookEntryLocalService.addGuestbookEntry(
	                serviceContext.getUserId(), guestbookId, userName, email,
	                message, serviceContext);

	            response.setRenderParameter("guestbookId", Long.toString(guestbookId));

	            System.out.println("edit entry");
	            SessionMessages.add(request, "entryAdded");
	
	        } catch (Exception e) {
	            System.out.println(e);
	
	            PortalUtil.copyRequestParameters(request, response);
	
	            response.setRenderParameter("mvcPath", "/guestbook/edit_entry.jsp");
	            SessionErrors.add(request, e.getClass().getName());
	            }
	        }
	}
	
	
	public void deleteEntry(ActionRequest request, ActionResponse response) throws PortalException {
        long entryId = ParamUtil.getLong(request, "entryId");
        long guestbookId = ParamUtil.getLong(request, "guestbookId");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
            GuestbookEntry.class.getName(), request);

        try {

            response.setRenderParameter("guestbookId", Long.toString(guestbookId));
            _guestbookEntryLocalService.deleteGuestbookEntry(entryId);
            SessionMessages.add(request, "entryDeleted");
        } catch (Exception e) {
        	Logger.getLogger(GuestbookPortlet.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        	SessionErrors.add(request, e.getClass().getName());
        }
        
	}
	
	
	
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
    throws IOException, PortletException {

    try {
        ServiceContext serviceContext = ServiceContextFactory.getInstance(Guestbook.class.getName(), renderRequest);

        long groupId = serviceContext.getScopeGroupId();

        long guestbookId = ParamUtil.getLong(renderRequest, "guestbookId");


        List<Guestbook> guestbooks = _guestbookLocalService.getGuestbooks(groupId);
        

        if (guestbooks.isEmpty()) {

        	Guestbook guestbook = _guestbookLocalService.addGuestbook(serviceContext.getUserId(), "Main", serviceContext);
            guestbookId = guestbook.getGuestbookId();
            
        }

        if (guestbookId == 0) {
            guestbookId = guestbooks.get(0).getGuestbookId();
        }

        renderRequest.setAttribute("guestbookId", guestbookId);
        
        System.out.println("render");
    }
    catch (Exception e) {
        throw new PortletException(e);
    }

    super.render(renderRequest, renderResponse);
}
	
}