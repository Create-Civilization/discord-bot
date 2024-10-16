import { Client, Collection, GatewayIntentBits, EmbedBuilder, Partials, Embed} from 'discord.js';

function embedMaker({ colorHex, title, description, footer = {}, author = {} }) {
    console.log(colorHex);
    const newEmbed = new EmbedBuilder()
        //Required
        .setColor(colorHex)
        .setTitle(title)
        .setDescription(description);

    //Optional
    if (footer.text || footer.iconURL) {
        newEmbed.setFooter({ text: footer.text || undefined, iconURL: footer.iconURL || undefined });
    }

    if (author.name || author.iconURL || author.clickableURL) {
        newEmbed.setAuthor({ 
            name: author.name || undefined, 
            iconURL: author.iconURL || undefined, 
            url: author.clickableURL || undefined 
        });
    }

    return newEmbed;
}

export { embedMaker };
