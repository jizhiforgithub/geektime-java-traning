package com.jizhi.geektime.jmx.jolokia;

import com.jizhi.geektime.jmx.mbean.My;

import javax.management.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 2021/3/16
 * jizhi7
 **/
public class JMXServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        registerMBean(new My());
    }

    private void registerMBean(Object mBean) {
        MBeanServer server = MBeanServerFactory.createMBeanServer("com.jizhi.jmx.common");
        try {
            ObjectName configuration = new ObjectName(mBean.getClass().getPackage().getName() + ":type=" + mBean.getClass().getSimpleName());
            server.registerMBean(mBean, configuration);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        }
    }

}
