/*
 * MIT License
 *
 * Copyright (c) 2020 WarCraft <https://github.com/WarCraft>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.warcraft.chat.channel

import java.util.UUID
import java.util.logging.Logger

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.{ColorCode, Message}
import gg.warcraft.monolith.api.core.auth.Principal
import gg.warcraft.monolith.api.core.command.Command
import gg.warcraft.monolith.api.core.event.Event
import gg.warcraft.monolith.api.player.{
  Player, PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent,
  PlayerService
}

case class GlobalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    format: String,
    permission: Option[String]
)(implicit
    logger: Logger,
    playerService: PlayerService,
    profileService: ProfileService,
    messageAdapter: MessageAdapter
) extends Channel
    with Event.Handler {
  private final val missingPermissions =
    "You do not have the required permissions to join [%s]."
  private final val successfullyJoined =
    "You have joined [%s]."
  private final val homeChannelPlayersOnly =
    "Only players can set their home channel."

  private var recipients: Set[UUID] = Set.empty

  private def authenticatePlayer(principal: Principal): Boolean = {
    def fixMissing(): Unit = {
      logger warning s"${principal.id} was not in $name, but channel has no permission!"
      recipients += principal.id
      val message = Message.server(successfullyJoined.format(name))
      messageAdapter.send(message, principal.id)
    }

    if (!recipients.contains(principal.id)) {
      permission match {
        case Some(permission) =>
          if (principal.hasPermission(permission)) {
            fixMissing(); true
          } else {
            val message = Message.server(missingPermissions.format(name))
            messageAdapter.send(message, principal.id); false
          }

        case _ => fixMissing(); true
      }
    } else true
  }

  override def handle(
      principal: Principal,
      command: Command,
      args: String*
  ): Command.Result = principal match {
    case player: Player =>
      val authenticated = authenticatePlayer(principal)
      if (authenticated) {
        if (args.isEmpty) makeHome(player.id)
        else broadcast(principal, args.mkString(" "), recipients)
      }
      Command.success

    case _ =>
      if (args.isEmpty) logger.info(homeChannelPlayersOnly)
      else broadcast(principal, args.mkString(" "), recipients)
      Command.success
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerConnectEvent            => handlePlayerConnect(it)
    case it: PlayerPermissionsChangedEvent => handlePlayerPermissions(it)
    case it: PlayerDisconnectEvent         => handlePlayerDisconnect(it)
    case _                                 =>
  }

  private def handlePlayerConnect(event: PlayerConnectEvent): Unit = {
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(event.player.id)
        if (player.hasPermission(permission)) recipients += event.player.id
      case None => recipients += event.player.id
    }
  }

  private def handlePlayerPermissions(event: PlayerPermissionsChangedEvent): Unit = {
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(event.player.id)
        if (recipients.contains(event.player.id)) {
          if (!player.hasPermission(permission)) recipients -= event.player.id
        } else if (player.hasPermission(permission)) recipients += event.player.id
      case None =>
    }
  }

  private def handlePlayerDisconnect(event: PlayerDisconnectEvent): Unit = {
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(event.player.id)
        if (player.hasPermission(permission)) recipients -= event.player.id
      case None => recipients -= event.player.id
    }
  }
}
