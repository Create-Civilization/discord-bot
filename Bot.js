import { Client, GatewayIntentBits } from 'discord.js';
import configJson from './config.json' with {type: 'json'}
import axios from 'axios';
const client = new Client({ intents: [GatewayIntentBits.Guilds] });
import fs from 'fs'
import path from 'path';

const token = configJson.token

client.on('ready', () => {
  console.log(`Logged in as ${client.user.tag}!`);
});

client.on('interactionCreate', async interaction => {
  if (!interaction.isChatInputCommand()) return;

  if (interaction.commandName === 'ping') {
    await interaction.reply('Pong!');
  }

  if (interaction.commandName === 'add_to_showcase') {
    const creator = interaction.options.get('creator')
    const title = interaction.options.get('title')
    const image = interaction.options.get('image')

    if (!image) {
        await interaction.followUp('No image was provided.');
        return;
    }

    downloadImage(image.attachment.url, './public_html/creationIMGs', `${title.value}.png`);

    


    console.log(image.attachment.url)

    const newEntry = {
        creator: creator.value,
        imgSRC: `creationIMGs\\${title.value}.png`,
        creationName: title.value,
        dateAdded: Date.now()
    };

    addEntry(newEntry)
    await interaction.reply('Successfully added new entry and updated the file.');
    await interaction.followUp('Saved as ' + title.value + '.png in public_html/creationIMGs');
  }
});


function addEntry(newEntry) {
    const filePath = path.join('public_html', 'creations.json'); 

    fs.readFile(filePath, 'utf8', (err, data) => {
        if (err) {
            console.error('Error reading file:', err);
            return;
        }
        let jsonArray;
        try {
            jsonArray = JSON.parse(data);
        } catch (parseError) {
            console.error('Error parsing JSON:', parseError);
            return;
        }

        jsonArray.push(newEntry);

        fs.writeFile(filePath, JSON.stringify(jsonArray, null, 2), 'utf8', (writeErr) => {
            if (writeErr) {
                console.error('Error writing file:', writeErr);
                return;
            }
            console.log('Successfully added new entry and updated the file.');
        });
    });
}
function downloadImage(imageUrl, saveDir, fileName) {
    // Create the directory if it doesn't exist
    if (!fs.existsSync(saveDir)) {
      fs.mkdirSync(saveDir, { recursive: true });
    }
  
    // Download the image
    axios({
      url: imageUrl,
      method: 'GET',
      responseType: 'stream',
    })
      .then((response) => {
        // Define the full path for the saved image
        const filePath = path.join(saveDir, fileName);
  
        // Save the image to the specified directory
        response.data.pipe(fs.createWriteStream(filePath));
  
        // Log when the download is complete
        response.data.on('end', () => {
          console.log(`Image saved to ${filePath}`);
        });
  
        // Handle any errors during the save process
        response.data.on('error', (err) => {
          console.error('Error saving the image:', err);
        });
      })
      .catch((error) => {
        console.error('Error downloading the image:', error);
      });
}




client.login(token);