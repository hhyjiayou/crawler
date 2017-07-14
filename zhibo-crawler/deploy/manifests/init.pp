class xbox {
  include config

#  file { [  "/home/work/data/",
#            "/home/work/data/scribe/",
#            "/home/work/data/scribe/account/",
#            "${config::basedir}/conf/" ] :
#    ensure => directory,
#  } ->
#
#  file { "${config::basedir}/conf/${config::mod_name}_scribe.conf":
#    content => template("scribe.conf.erb"),
#  }

}

include xbox
