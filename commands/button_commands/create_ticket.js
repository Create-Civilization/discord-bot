export default {
    customId: 'create_ticket',  // This should match the custom ID set in the button
    run: async (client, interaction) => {
      // Code to run when the button is clicked
      await interaction.reply({ content: 'Ticket created!' });
    }
  };
  