import { PermissionsBitField } from "discord.js";
import axios from 'axios';
import fs from 'fs';
import path from 'path';
import { exec } from "child_process";
import { SlashCommandBuilder} from "discord.js";

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
  
  // Function to download an image and save it
  function downloadImage(imageUrl, saveDir, fileName) {
    if (!fs.existsSync(saveDir)) {
      fs.mkdirSync(saveDir, { recursive: true });
    }
  
    axios({
      url: imageUrl,
      method: 'GET',
      responseType: 'stream',
    })
      .then(response => {
        const filePath = path.join(saveDir, fileName);
        response.data.pipe(fs.createWriteStream(filePath));
  
        response.data.on('end', () => {
          console.log(`Image saved to ${filePath}`);
        });
  
        response.data.on('error', (err) => {
          console.error('Error saving the image:', err);
        });
      })
      .catch((error) => {
        console.error('Error downloading the image:', error);
      });
  }

export default {
    data: new SlashCommandBuilder()
        .setName('add_to_showcase')
        .setDescription('Adds an entry to the showcase'),
    async execute(client,interaction) {
        const requestingMember = await interaction.guild.members.fetch(interaction.user.id);
        if (!requestingMember.permissions.has(PermissionsBitField.Flags.KickMembers) || interaction.guild.id == !'1268369952348442775') {
            await interaction.reply({ content: 'You cannot use this command', ephemeral: true });
        return;}
        const creator = interaction.options.get('creator');
        const title = interaction.options.get('title');
        const image = interaction.options.get('image');
        if (!image){
            await interaction.reply('no image provided');
            return;
        }
        downloadImage(image.attachment.url, './public_html/creationIMGs', `${title.value}.png`);
        const newEntry = {
          creator: creator.value,
          imgSRC: `creationIMGs\\${title.value}.png`,
          creationName: title.value,
          dateAdded: Math.floor(Date.now() / 1000),
        };
    
        addEntry(newEntry);
        await interaction.reply('Successfully added new entry and updated the file.');
        await interaction.followUp('Saved as ' + title.value + '.png in public_html/creationIMGs');

        
    }
}