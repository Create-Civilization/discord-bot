const { Events } = require('discord.js');
const { updateStatusTask, checkStaleTickets, checkForIgnChanges, checkForUnbans } = require('../other_functions/tasks.js');

module.exports = {
    name: Events.ClientReady,
    once: false,
    async execute(client) {
        
        setInterval(async () => {
            client.isServerAlive = await updateStatusTask(client)
            console.log(client.isServerAlive)
        }, 5000);

        setInterval(async () => {
            await checkStaleTickets(client);
        }, 5000);

        setInterval(async () => {
            await checkForUnbans(client);
        }, 5000)

        setInterval(async () => {
            await checkForIgnChanges(client);
        }, 3600000);
    }
}