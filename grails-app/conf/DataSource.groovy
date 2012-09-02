hibernate {
  cache.use_second_level_cache = true
  cache.use_query_cache = true
  cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}

// environment specific settings
environments {
  development {
    dataSource {
      pooled = true
      driverClassName = "org.hsqldb.jdbcDriver"
      username = "sa"
      password = ""
      dbCreate = "update"
      url = "jdbc:hsqldb:file:devDb;shutdown=true"
    }
  }

  test {
    dataSource {
      pooled = true
      driverClassName = "org.hsqldb.jdbcDriver"
      username = "sa"
      password = ""
      dbCreate = "update"
      url = "jdbc:hsqldb:file:testDb;shutdown=true"
    }
  }

  production {
    dataSource {
      pooled = true
      driverClassName = "com.mysql.jdbc.Driver"
      dbCreate = "none"
      url = "jdbc:mysql://mysql.swervesoft.net:3306/hockey?useUnicode=yes&characterEncoding=UTF-8"
      username = "hockey"
      password = "hockey"
      properties {
        //run the evictor every 30 minutes and evict any connections older than 30 minutes.
        minEvictableIdleTimeMillis=1800000
        timeBetweenEvictionRunsMillis=1800000
        numTestsPerEvictionRun=3
        //test the connection while its idle, before borrow and return it
        testOnBorrow=true
        testWhileIdle=true
        testOnReturn=true
        validationQuery="SELECT 1"
      }
    }
  }
}

