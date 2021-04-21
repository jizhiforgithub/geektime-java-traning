package com.jizhi.geektime.inject;

import com.jizhi.geektime.function.ThrowableAction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Set;

/**
 * JNDI实现的组件仓库
 *
 * @author jizhi7
 * @since 1.0
 */
public class JndiComponentRepository extends AbstractComponentRepository {

    /**
     * JNDI的基本目录
     */
    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    /**
     * JNDI的上下文
     */
    private Context envContext;

    @Override
    protected Set<String> listComponentNames() {
        return null;
    }

    @Override
    protected Object doGetComponent(String name) {
        return null;
    }

    /**
     * 初始化EnvContext
     * @throws RuntimeException
     */
    private void initEnvContext() throws RuntimeException {
        if (this.envContext != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(context);
        }
    }

    private void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }
}
