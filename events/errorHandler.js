import {Events} from 'discord.js';

export default {
    name: Events.ClientError,
    once: false,
    async execute(error) {
        console.error('Client error:', error);
    },
}