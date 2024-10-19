import {Events} from 'discord.js';
import { checkCrashTask, updateStatusTask } from './other_functions/tasks.js';
let isServerAlive = false;

export default {
    name: Events.Ready,
    once: false,
    execute(client) {
        console.log(`Logged in as ${client.user.tag}!`);

        setInterval(async () => {
            isServerAlive = await updateStatusTask(client)
            console.log(isServerAlive)
        }, 5000);
      
        setInterval(async () => {
          await checkCrashTask(client);
      }, 5 * 60 * 1000);
    }
}