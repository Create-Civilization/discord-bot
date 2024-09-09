
export default  {
    name: 'ping',
    aliases: [],
    description:'pong!!!!!!!!!!!!!!',
    usage:'testing',
    run: async (client,interaction) => {
        await interaction.reply('Pong!!!!');

    }

}