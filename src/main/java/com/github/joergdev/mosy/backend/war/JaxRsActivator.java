package com.github.joergdev.mosy.backend.war;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.ResponseMessageLevel;
import com.github.joergdev.mosy.backend.api.APIUtils;
import com.github.joergdev.mosy.backend.bl.system.BootIntern;
import com.github.joergdev.mosy.backend.persistence.EntityManagerProviderService;

@ApplicationPath("/")
public class JaxRsActivator extends Application
{
  private static final Logger LOG = Logger.getLogger(JaxRsActivator.class);

  public JaxRsActivator()
  {
    LOG.info("Booting Application MoSy Backend");

    // set EntityManagerProvider
    EntityManagerProviderService.getInstance().setEntityManagerProvider(new EntityManagerProviderImpl());

    doSystemBoot();

    LOG.info("Booted Application MoSy Backend");
  }

  private static void doSystemBoot()
  {
    Response response = APIUtils.executeBL(null, new EmptyResponse(), new BootIntern());

    EmptyResponse emptyResponse = (EmptyResponse) response.getEntity();

    if (!emptyResponse.isStateOK())
    {
      emptyResponse.getMessagesForLevel(ResponseMessageLevel.FATAL, ResponseMessageLevel.ERROR)
          .forEach(m -> LOG.error(m.getFullMessage()));

      throw new IllegalStateException("Booting Application MoSy Backend FAILED");
    }
  }
}