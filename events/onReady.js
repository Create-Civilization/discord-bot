const { Events } = require('discord.js');
const { checkCrashTask, updateStatusTask } = require('../other_functions/tasks.js');

module.exports = {
    name: Events.ClientReady,
    once: true,
    async execute(client) {
        console.log(`Bot ready! ${client.user.tag}`);
    }
}