package at.tfr.pfad.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

import org.junit.Test;
import org.omnifaces.util.Messages;

public class TestOmnifaces {

	@Test
	public void testMessages() throws Exception {
		final MyFacesContext mfc = init();
		
		String test = "hallo {1}";
		Messages.addError("TEST", test, null);
		Iterator<FacesMessage> msgs = FacesContext.getCurrentInstance().getMessages("TEST");
		FacesMessage fm = msgs.next();
		mfc.facesMessages.clear();

		Messages.addError("TEST", test, "blubb");
		msgs = FacesContext.getCurrentInstance().getMessages("TEST");
		fm = msgs.next();
		mfc.facesMessages.clear();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadMessages() throws Exception {
		final MyFacesContext mfc = init();
		
		String test = "hallo {1}{gaga}";
		Messages.addError("TEST", test, null);
	}
	private MyFacesContext init() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method m = FacesContext.class.getDeclaredMethod("setCurrentInstance",  FacesContext.class);
		m.setAccessible(true);
		final MyFacesContext mfc = new MyFacesContext();
		m.invoke(null, mfc);
		return mfc;
	}
	
	class MyFacesContext extends FacesContext {
		
		Map<String, List<FacesMessage>> facesMessages = new HashMap<>();

		@Override
		public Application getApplication() {
			return null;
		}

		@Override
		public Iterator<String> getClientIdsWithMessages() {
			return null;
		}

		@Override
		public ExternalContext getExternalContext() {
			return null;
		}

		@Override
		public Severity getMaximumSeverity() {
			return null;
		}

		@Override
		public Iterator<FacesMessage> getMessages() {
			return facesMessages.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList()).iterator();
		}

		@Override
		public Iterator<FacesMessage> getMessages(String clientId) {
			return facesMessages.get(clientId).iterator();
		}

		@Override
		public RenderKit getRenderKit() {
			return null;
		}

		@Override
		public boolean getRenderResponse() {
			return false;
		}

		@Override
		public boolean getResponseComplete() {
			return false;
		}

		@Override
		public ResponseStream getResponseStream() {
			return null;
		}

		@Override
		public void setResponseStream(ResponseStream responseStream) {
		}

		@Override
		public ResponseWriter getResponseWriter() {
			return null;
		}

		@Override
		public void setResponseWriter(ResponseWriter responseWriter) {
		}

		@Override
		public UIViewRoot getViewRoot() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setViewRoot(UIViewRoot root) {
		}

		@Override
		public synchronized void addMessage(String clientId, FacesMessage message) {
			List<FacesMessage> list = facesMessages.get(clientId);
			if (list == null) {
				list = new ArrayList<>();
				facesMessages.put(clientId, list);
			}
			list.add(message);
		}

		@Override
		public void release() {
		}

		@Override
		public void renderResponse() {
		}

		@Override
		public void responseComplete() {
		}
		
	}
}
