package org.cloudfoundry.samples;

import org.cloudfoundry.runtime.env.CloudEnvironment;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CloudEnvironment cloudEnvironment = new CloudEnvironment();

		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("Hello from " +  cloudEnvironment.getInstanceInfo().getHost()+ ":" + cloudEnvironment.getInstanceInfo().getPort() );
		writer.close();
	}
}
