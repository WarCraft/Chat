package gg.warcraft.chat.akka

import java.util.Properties

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.akka.channel.{GlobalChannel, LocalChannel}
import gg.warcraft.chat.akka.profile.ProfileService
import gg.warcraft.monolith.api.core.command.CommandService
import gg.warcraft.monolith.api.core.event.EventService
import io.getquill.{SnakeCase, SqliteJdbcContext}
import io.getquill.context.jdbc.JdbcContext

object ChatSystem {
  def apply(config: ChatConfig)(
      implicit commandService: CommandService,
      eventService: EventService
  ): Behavior[Unit] = Behaviors.setup { context =>
    // setup database
    val databaseProps = new Properties()
    databaseProps.setProperty("driverClassName", config.jdbcDriver)
    databaseProps.setProperty("jdbcUrl", config.jdbcUrl)
    val databaseConfig = ConfigFactory.parseProperties(databaseProps)
    implicit val database: JdbcContext[_, _] =
      new SqliteJdbcContext(SnakeCase, databaseConfig)

    // setup profiles
    implicit val profileService: ActorRef[ProfileService.Command] = context.spawn(
      ProfileService(config.defaultTag, config.defaultChannel),
      ProfileService.getClass.getSimpleName
    )

    // setup global channels
    val globalChannels = config.globalChannels map { channel =>
      import channel._
      context.spawn(
        GlobalChannel(name, aliases, shortcut, color, format, permission),
        s"$name:${GlobalChannel.getClass.getSimpleName}"
      )
    }

    // setup local channels
    val localChannels = config.localChannels map { channel =>
      import channel._
      context.spawn(
        LocalChannel(name, aliases, shortcut, color, format, radius),
        s"$name:${LocalChannel.getClass.getSimpleName}"
      )
    }

    Behaviors.receiveSignal {
      case (_, PostStop) =>
        database.close()
        Behaviors.stopped
    }
  }
}
