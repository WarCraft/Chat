package gg.warcraft.chat.channel

import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.util.ColorCode

case class LocalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    formatString: String,
    radius: Float
) extends Channel {
  private final val playersOnly = "Only players can talk in local chat channels."

  /*
 Collection<UUID> getNearbyPlayerIds(Location location) {
        List<Entity> nearbyEntities = entityQueryService.getNearbyEntities(location, channel.getRadius());
        return nearbyEntities.stream()
                .filter(entity -> entity instanceof Player)
                .map(Entity::getId)
                .collect(Collectors.toList());
    }

    boolean onPlayerChatCommand(CommandSender sender, String text) {
        ChatProfile senderProfile = sender.isPlayer()
                ? profileQueryService.getChatProfile(sender.playerId().get())
                : profileQueryService.getConsoleChatProfile();
        String formattedText = formatter.format(channel, senderProfile, text);
        Message message = messageFactory.createMessage(channel, sender, text, formattedText);
        Player player = playerQueryService.getPlayer(sender.playerId().get());
        Collection<UUID> recipientIds = getNearbyPlayerIds(player.getLocation());
        recipientIds.forEach(recipient -> messageCommandService.sendMessageToPlayer(message, recipient));
        if (recipientIds.size() == 1) {
            Message muteMessage = messageFactory.createMuteMessage();
            messageCommandService.sendMessageToPlayer(muteMessage, sender.playerId().get());
        }

        logger.log(message);
        return true;
    }

 */

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      if(cmd.args.isEmpty) {
        // profileCommandService.setHomeChannel(sender.playerId().get(), channel);
        true
      } else {
        // String text = command.args().mkString(" ");
        // onPlayerChatCommand(sender, text);
        true
      }

    case _ =>
      // TODO send playerOnly to console
      true
  }
}
