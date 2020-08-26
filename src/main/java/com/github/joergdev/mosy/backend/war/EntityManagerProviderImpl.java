package com.github.joergdev.mosy.backend.war;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import com.github.joergdev.mosy.backend.persistence.EntityManagerProvider;

public class EntityManagerProviderImpl implements EntityManagerProvider
{
  public EntityManager getEntityManager()
  {
    try
    {
      EntityManager em = (EntityManager) new InitialContext().lookup("java:/mosy/entityManager");

      UserTransaction transaction = getTransaction();

      int txStatus = transaction.getStatus();

      if (Status.STATUS_NO_TRANSACTION == txStatus)
      {
        transaction.begin();
      }

      return em;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  private UserTransaction getTransaction()
    throws NamingException
  {
    UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
    return transaction;
  }

  public void releaseEntityManager(EntityManager em)
  {
    try
    {
      UserTransaction transaction = getTransaction();

      if (transaction != null && Status.STATUS_ACTIVE == transaction.getStatus())
      {
        transaction.commit();
      }
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public void rollbackEntityManager(EntityManager em)
  {
    try
    {
      UserTransaction transaction = getTransaction();

      if (transaction != null && (Status.STATUS_ACTIVE == transaction.getStatus()
                                  || Status.STATUS_MARKED_ROLLBACK == transaction.getStatus()))
      {
        transaction.rollback();
      }
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  public boolean isContainerManaged()
  {
    return true;
  }
}