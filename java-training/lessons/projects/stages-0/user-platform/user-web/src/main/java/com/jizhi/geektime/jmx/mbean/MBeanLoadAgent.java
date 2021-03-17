package com.jizhi.geektime.jmx.mbean;

import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * 加载mbean
 *
 * @author jizhi7
 * @since 1.0
 **/
public class MBeanLoadAgent {

    public void loadMBean() {
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            Hello hello = new Hello("");
            ObjectName name = ObjectName.getInstance("com.jizhi.geektime.jmx.mbean:type=Hello");
            platformMBeanServer.registerMBean(hello, name);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }

}
