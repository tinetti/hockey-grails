dataSource {
  pooled = true
  driverClassName = "org.hsqldb.jdbcDriver"
  username = "sa"
  password = ""
}
hibernate {
  cache.use_second_level_cache = true
  cache.use_query_cache = true
  cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
  development {
    dataSource {
      dbCreate = "update"
      url = "jdbc:hsqldb:file:devDb;shutdown=true"
    }
  }
  test {
    dataSource {
      dbCreate = "update"
      url = "jdbc:hsqldb:file:testDb;shutdown=true"
    }
  }
//  production {
//    dataSource {
//      dbCreate = "create"
//      url = "jdbc:hsqldb:file:hockeyDb;shutdown=true"
//    }
//  }
  production {
    dataSource {
      dbCreate = "none"
      url = "jdbc:mysql://mysql.swervesoft.net:3306/hockey?useUnicode=yes&characterEncoding=UTF-8"
      username = "hockey"
      password = "hockey"
    }
  }
}
