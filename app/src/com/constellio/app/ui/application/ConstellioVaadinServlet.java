package com.constellio.app.ui.application;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.constellio.app.services.factories.ConstellioFactories;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")
@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = ConstellioUI.class)
public class ConstellioVaadinServlet extends VaadinServlet {

	boolean initialized = false;
	Thread initThread;

	@Override
	public void init(ServletConfig servletConfig)
			throws ServletException {
		System.out.println("ConstellioVaadinServlet.init");
		super.init(servletConfig);

		initThread = new Thread() {
			@Override
			public void run() {
				System.out.println("ConstellioVaadinServlet.init>ConstellioFactories.getInstance()...");
				ConstellioFactories.getInstance();
				initialized = true;
			}
		};
		initThread.start();

	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!initialized) {
			System.out.println("ConstellioVaadinServlet.service (waiting)");
			try {
				initThread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println("ConstellioVaadinServlet.service");
		super.service(request, response);
	}

	/**
	 * Adapted to support responsive design.
	 *
	 * See https://vaadin.com/forum#!/thread/1676923
	 * @see com.vaadin.server.VaadinServlet#servletInitialized()
	 */

	@Override
	protected final void servletInitialized()
			throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(new ConstellioSessionInitListener());
	}

	public static ConstellioVaadinServlet getCurrent() {
		return (ConstellioVaadinServlet) VaadinServlet.getCurrent();
	}

}
