export default {
    name: 'coinflip',
    run: async(client, interaction) =>{
        const num = Math.floor(Math.random()*11)
        if (num%2 === 0){
            await interaction.reply({content:"Heads!!!",ephmeral: false})
        }
        else {
            await interaction.reply({content:"Tails!!!",ephmeral: false})
        }
    }
}