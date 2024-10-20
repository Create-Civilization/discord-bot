const { Events } = require('discord.js');

module.exports = {
    name: Events.ClientError,
    once: false,
    async execute(client, error) {
        console.error('Client error:', error);
    },
};