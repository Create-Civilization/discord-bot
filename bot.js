const fs = require('fs');
const path = require('path');
const { Client, Collection, GatewayIntentBits, Partials } = require('discord.js');
const { initTicketDatabase } = require('./other_functions/ticketDatabaseFuncs.js');
const { initWhiteListDatabase } = require('./other_functions/whitelistDatabaseFuncs.js');

initTicketDatabase();
initWhiteListDatabase();

const configPath = path.join(__dirname, 'config.json');


const defaultConfig = {
    token: '',
    clientID: '',
    guildID: '', 
    craftyToken: '', 
    adminRolesIDS: [], 
    helpTicketChannelID: '',
    whitelistedRoleID: '',
    serverID: '',
    serverIP: '',
    serverPort: ''
};


const checkAndUpdateConfig = () => {
    if (!fs.existsSync(configPath)) {
        console.error("Config file not found! Creating a new config.json.");
        fs.writeFileSync(configPath, JSON.stringify(defaultConfig, null, 2));
        console.log("Created config.json with default values.");
        process.exit(1); 
    }

    const config = JSON.parse(fs.readFileSync(configPath));

    let updated = false;
    for (const [key, value] of Object.entries(defaultConfig)) {
        if (!(key in config)) {
            console.log(`Missing config option: ${key}. Adding it.`);
            config[key] = value; 
            updated = true;
        }
    }


    for (const [key, value] of Object.entries(defaultConfig)) {
        if (typeof value === 'string' && (key !== 'adminRolesIDS' && !config[key])) {
            console.log(`Config option '${key}' is missing or empty. Adding default value.`);
            config[key] = ''; 
            updated = true;
        } else if (Array.isArray(value) && !Array.isArray(config[key])) {
            console.log(`Config option '${key}' should be an array. Resetting to default value.`);
            config[key] = []; 
            updated = true;
        }
    }

    if (updated) {
        fs.writeFileSync(configPath, JSON.stringify(config, null, 2));
        console.log("Updated config.json");
        process.exit(1); 
    }

    return config; 
};

const config = checkAndUpdateConfig(); 

const client = new Client({
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMembers,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.DirectMessages,
    ],
    partials: [Partials.Channel]
});

let isServerAlive = false;
client.isServerAlive = isServerAlive;

client.commands = new Collection();
const commandsPath = path.join(__dirname, 'commands');
const commandFolders = fs.readdirSync(commandsPath);

for (const folder of commandFolders) {
    const commandsPath2 = path.join(commandsPath, folder);
    const commandFiles = fs.readdirSync(commandsPath2).filter(file => file.endsWith('.js'));
    for (const file of commandFiles) {
        const filePath = path.join(commandsPath2, file);
        const command = require(filePath);
        if ('data' in command && 'execute' in command) {
            console.log(`Loaded command: ${command.data.name}`);
            client.commands.set(command.data.name, command);
        } else {
            console.log(`[WARNING] The command at ${filePath} is missing a required "data" or "execute" property.`);
        }
    }
}

const eventsPath = path.join(__dirname, 'events');
const eventFiles = fs.readdirSync(eventsPath).filter(file => file.endsWith('.js'));

for (const file of eventFiles) {
    const filePath = path.join(eventsPath, file);
    const event = require(filePath);
    if (event.once) {
        console.log(`Loaded event: ${event.name}, once`);
        client.once(event.name, (...args) => event.execute(client, ...args));
    } else {
        console.log(`Loaded event: ${event.name}, not once`);
        client.on(event.name, (...args) => event.execute(client, ...args));
    }
}

client.login(config.token);
