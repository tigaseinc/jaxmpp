/*
 * Tigase XMPP Client Library
 * Copyright (C) 2006-2012 "Bartosz Małkowski" <bartosz.malkowski@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.jaxmpp.core.client.xmpp.modules.presence;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.Context;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.criteria.Criteria;
import tigase.jaxmpp.core.client.criteria.ElementCriteria;
import tigase.jaxmpp.core.client.eventbus.EventHandler;
import tigase.jaxmpp.core.client.eventbus.EventType;
import tigase.jaxmpp.core.client.eventbus.JaxmppEvent;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.AbstractStanzaModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.ContactAvailableHandler.ContactAvailableEvent;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.ContactChangedPresenceHandler.ContactChangedPresenceEvent;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.ContactUnavailableHandler.ContactUnavailableEvent;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.ContactUnsubscribedHandler.ContactUnsubscribedEvent;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.SubscribeRequestHandler.SubscribeRequestEvent;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceStore.Handler;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence.Show;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

/**
 * Module for handling presence information.
 */
public class PresenceModule extends AbstractStanzaModule<Presence> {

	/**
	 * Event fired before each presence sent by client.
	 */
	public interface BeforePresenceSendHandler extends EventHandler {

		public static class BeforePresenceSendEvent extends JaxmppEvent<BeforePresenceSendHandler> {

			public static final EventType<BeforePresenceSendHandler> TYPE = new EventType<BeforePresenceSendHandler>();

			public BeforePresenceSendEvent(SessionObject sessionObject) {
				super(TYPE, sessionObject);
			}

			@Override
			protected void dispatch(BeforePresenceSendHandler handler) {
				handler.onBeforePresenceSend(sessionObject);
			}

		}

		void onBeforePresenceSend(SessionObject sessionObject);
	}

	/**
	 * Event fired when contact (understood as bare JID) becomes available.
	 * Fired when first resource of JID becomes available.
	 */
	public interface ContactAvailableHandler extends EventHandler {

		public static class ContactAvailableEvent extends JaxmppEvent<ContactAvailableHandler> {

			public static final EventType<ContactAvailableHandler> TYPE = new EventType<ContactAvailableHandler>();

			private JID jid;

			private Integer priority;

			private Show show;

			private Presence stanza;

			private String status;

			public ContactAvailableEvent(SessionObject sessionObject, Presence presence, JID jid, Show show,
					String statusMessage, Integer priority) {
				super(TYPE, sessionObject);
				this.stanza = presence;
				this.jid = jid;
				this.show = show;
				this.status = statusMessage;
				this.priority = priority;
			}

			@Override
			protected void dispatch(ContactAvailableHandler handler) {
				handler.onContactAvailable(sessionObject, stanza, jid, show, status, priority);
			}

			public JID getJid() {
				return jid;
			}

			public Integer getPriority() {
				return priority;
			}

			public Show getShow() {
				return show;
			}

			public Presence getStanza() {
				return stanza;
			}

			public String getStatus() {
				return status;
			}

			public void setJid(JID jid) {
				this.jid = jid;
			}

			public void setPriority(Integer priority) {
				this.priority = priority;
			}

			public void setShow(Show show) {
				this.show = show;
			}

			public void setStanza(Presence stanza) {
				this.stanza = stanza;
			}

			public void setStatus(String status) {
				this.status = status;
			}

		}

		void onContactAvailable(SessionObject sessionObject, Presence stanza, JID jid, Show show, String status,
				Integer priority);
	}

	/**
	 * Event fired when contact changed his presence.
	 */
	public interface ContactChangedPresenceHandler extends EventHandler {

		public static class ContactChangedPresenceEvent extends JaxmppEvent<ContactChangedPresenceHandler> {

			public static final EventType<ContactChangedPresenceHandler> TYPE = new EventType<ContactChangedPresenceHandler>();

			private JID jid;

			private Integer priority;

			private Show show;

			private Presence stanza;

			private String status;

			public ContactChangedPresenceEvent(SessionObject sessionObject, Presence presence, JID jid, Show show,
					String statusMessage, Integer priority) {
				super(TYPE, sessionObject);
				this.stanza = presence;
				this.jid = jid;
				this.show = show;
				this.status = statusMessage;
				this.priority = priority;
			}

			@Override
			protected void dispatch(ContactChangedPresenceHandler handler) {
				handler.onContactChangedPresence(sessionObject, stanza, jid, show, status, priority);
			}

			public JID getJid() {
				return jid;
			}

			public Integer getPriority() {
				return priority;
			}

			public Show getShow() {
				return show;
			}

			public Presence getStanza() {
				return stanza;
			}

			public String getStatus() {
				return status;
			}

			public void setJid(JID jid) {
				this.jid = jid;
			}

			public void setPriority(Integer priority) {
				this.priority = priority;
			}

			public void setShow(Show show) {
				this.show = show;
			}

			public void setStanza(Presence stanza) {
				this.stanza = stanza;
			}

			public void setStatus(String status) {
				this.status = status;
			}

		}

		void onContactChangedPresence(SessionObject sessionObject, Presence stanza, JID jid, Show show, String status,
				Integer priority);
	}

	/**
	 * Event fired when contact (understood as bare JID) goes offline. Fired
	 * when no more resources are available.
	 */
	public interface ContactUnavailableHandler extends EventHandler {

		public static class ContactUnavailableEvent extends JaxmppEvent<ContactUnavailableHandler> {

			public static final EventType<ContactUnavailableHandler> TYPE = new EventType<ContactUnavailableHandler>();

			private JID jid;

			private Presence stanza;

			private String status;

			public ContactUnavailableEvent(SessionObject sessionObject, Presence presence, JID jid, String statusMessage) {
				super(TYPE, sessionObject);
				this.stanza = presence;
				this.jid = jid;
				this.status = statusMessage;
			}

			@Override
			protected void dispatch(ContactUnavailableHandler handler) {
				handler.onContactUnavailable(sessionObject, stanza, jid, status);
			}

			public JID getJid() {
				return jid;
			}

			public Presence getStanza() {
				return stanza;
			}

			public String getStatus() {
				return status;
			}

			public void setJid(JID jid) {
				this.jid = jid;
			}

			public void setStanza(Presence stanza) {
				this.stanza = stanza;
			}

			public void setStatus(String status) {
				this.status = status;
			}

		}

		void onContactUnavailable(SessionObject sessionObject, Presence stanza, JID jid, String status);
	}

	/**
	 * Event fired when contact is unsubscribed.
	 */
	public interface ContactUnsubscribedHandler extends EventHandler {

		public static class ContactUnsubscribedEvent extends JaxmppEvent<ContactUnsubscribedHandler> {

			public static final EventType<ContactUnsubscribedHandler> TYPE = new EventType<ContactUnsubscribedHandler>();

			private BareJID jid;

			private Presence stanza;

			public ContactUnsubscribedEvent(SessionObject sessionObject, Presence presence, BareJID bareJID) {
				super(TYPE, sessionObject);
				this.stanza = presence;
				this.jid = bareJID;
			}

			@Override
			protected void dispatch(ContactUnsubscribedHandler handler) {
				handler.onContactUnsubscribed(sessionObject, stanza, jid);
			}

			public BareJID getJid() {
				return jid;
			}

			public Presence getStanza() {
				return stanza;
			}

			public void setJid(BareJID jid) {
				this.jid = jid;
			}

			public void setStanza(Presence stanza) {
				this.stanza = stanza;
			}

		}

		void onContactUnsubscribed(SessionObject sessionObject, Presence stanza, BareJID jid);
	}

	/**
	 * Event fired when subscription request is received.
	 */
	public interface SubscribeRequestHandler extends EventHandler {

		public static class SubscribeRequestEvent extends JaxmppEvent<SubscribeRequestHandler> {

			public static final EventType<SubscribeRequestHandler> TYPE = new EventType<SubscribeRequestHandler>();

			private BareJID jid;

			private Presence stanza;

			public SubscribeRequestEvent(SessionObject sessionObject, Presence presence, BareJID bareJID) {
				super(TYPE, sessionObject);
				this.stanza = presence;
				this.jid = bareJID;
			}

			@Override
			protected void dispatch(SubscribeRequestHandler handler) {
				handler.onSubscribeRequest(sessionObject, stanza, jid);
			}

			public BareJID getJid() {
				return jid;
			}

			public Presence getStanza() {
				return stanza;
			}

			public void setJid(BareJID jid) {
				this.jid = jid;
			}

			public void setStanza(Presence stanza) {
				this.stanza = stanza;
			}

		}

		void onSubscribeRequest(SessionObject sessionObject, Presence stanza, BareJID jid);
	}

	public static final Criteria CRIT = ElementCriteria.name("presence");

	public PresenceModule(Context context) {
		super(context);
		this.context.getSessionObject().getPresence().setHandler(new Handler() {

			@Override
			public void onOffline(Presence i) throws JaxmppException {
				contactOffline(i, i.getFrom());
			}

			@Override
			public void setPresence(Show show, String status, Integer priority) throws XMLException, JaxmppException {
				PresenceModule.this.setPresence(show, status, priority);
			}
		});
	}

	protected void contactOffline(Presence i, final JID jid) throws JaxmppException {
		fireEvent(new ContactUnavailableEvent(context.getSessionObject(), i, jid, null));
	}

	@Override
	public Criteria getCriteria() {
		return CRIT;
	}

	@Override
	public String[] getFeatures() {
		return null;
	}

	public PresenceStore getPresence() {
		return this.context.getSessionObject().getPresence();
	}

	@Override
	public void process(final Presence presence) throws JaxmppException {
		final JID fromJid = presence.getFrom();
		log.finest("Presence received from " + fromJid + " :: " + presence.getAsString());
		if (fromJid == null)
			return;

		boolean availableOld = context.getSessionObject().getPresence().isAvailable(fromJid.getBareJid());
		context.getSessionObject().getPresence().update(presence);
		boolean availableNow = context.getSessionObject().getPresence().isAvailable(fromJid.getBareJid());

		final StanzaType type = presence.getType();

		if (type == StanzaType.unsubscribed) {
			fireEvent(new ContactUnsubscribedEvent(context.getSessionObject(), presence, presence.getFrom().getBareJid()));
		} else if (type == StanzaType.subscribe) {
			// subscribe
			log.finer("Subscribe from " + fromJid);
			fireEvent(new SubscribeRequestEvent(context.getSessionObject(), presence, presence.getFrom().getBareJid()));
		} else if (!availableOld && availableNow) {
			// sontact available
			log.finer("Presence online from " + fromJid);
			fireEvent(new ContactChangedPresenceEvent(context.getSessionObject(), presence, presence.getFrom(),
					presence.getShow(), presence.getStatus(), presence.getPriority()));
			fireEvent(new ContactAvailableEvent(context.getSessionObject(), presence, presence.getFrom(), presence.getShow(),
					presence.getStatus(), presence.getPriority()));
		} else if (availableOld && !availableNow) {
			// contact unavailable
			log.finer("Presence offline from " + fromJid);
			fireEvent(new ContactChangedPresenceEvent(context.getSessionObject(), presence, presence.getFrom(),
					presence.getShow(), presence.getStatus(), presence.getPriority()));
			fireEvent(new ContactUnavailableEvent(context.getSessionObject(), presence, presence.getFrom(),
					presence.getStatus()));
		} else {
			log.finer("Presence change from " + fromJid);
			fireEvent(new ContactChangedPresenceEvent(context.getSessionObject(), presence, presence.getFrom(),
					presence.getShow(), presence.getStatus(), presence.getPriority()));
		}
	}

	public void sendInitialPresence() throws XMLException, JaxmppException {
		Presence presence = Presence.create();

		if (context.getSessionObject().getProperty(SessionObject.NICKNAME) != null) {
			presence.setNickname((String) context.getSessionObject().getProperty(SessionObject.NICKNAME));
		}

		write(presence);
	}

	/**
	 * Sends own presence.
	 * 
	 * @param show
	 *            presence substate.
	 * @param status
	 *            human readable description of status.
	 * @param priority
	 *            priority.
	 */
	public void setPresence(Show show, String status, Integer priority) throws XMLException, JaxmppException {
		Presence presence = Presence.create();
		presence.setShow(show);
		presence.setStatus(status);
		presence.setPriority(priority);
		if (context.getSessionObject().getProperty(SessionObject.NICKNAME) != null) {
			presence.setNickname((String) context.getSessionObject().getProperty(SessionObject.NICKNAME));
		}

		write(presence);
	}

	/**
	 * Subscribe for presence.
	 * 
	 * @param jid
	 *            JID
	 */
	public void subscribe(JID jid) throws JaxmppException, XMLException {
		Presence p = Presence.create();
		p.setType(StanzaType.subscribe);
		p.setTo(jid);

		write(p);
	}

	public void subscribed(JID jid) throws JaxmppException, XMLException {
		Presence p = Presence.create();
		p.setType(StanzaType.subscribed);
		p.setTo(jid);

		write(p);
	}

	public void unsubscribe(JID jid) throws JaxmppException, XMLException {
		Presence p = Presence.create();
		p.setType(StanzaType.unsubscribe);
		p.setTo(jid);

		write(p);
	}

	public void unsubscribed(JID jid) throws JaxmppException, XMLException {
		Presence p = Presence.create();
		p.setType(StanzaType.unsubscribed);
		p.setTo(jid);

		write(p);
	}

}