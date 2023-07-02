package server.team33.global.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class AutoWiringSpringBeanJobFactory
    extends SpringBeanJobFactory implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanJobFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        beanJobFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        beanJobFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
