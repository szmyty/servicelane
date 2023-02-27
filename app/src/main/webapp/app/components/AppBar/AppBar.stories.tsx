import React from 'react';
import { ComponentStory, ComponentMeta } from '@storybook/react';

import AppBar from './AppBar';

// More on default export: https://storybook.js.org/docs/react/writing-stories/introduction#default-export
export default {
  title: 'Servicelane/AppBar',
  component: AppBar,
  // More on argTypes: https://storybook.js.org/docs/react/api/argtypes
  argTypes: {
    backgroundColor: { control: 'color' },
  },
} as ComponentMeta<typeof AppBar>;

// More on component templates: https://storybook.js.org/docs/react/writing-stories/introduction#using-args
const Template: ComponentStory<typeof AppBar> = () => (
  <AppBar account="Anonymous" isAuthenticated={false} isAdmin={false} currentLocale="" isInProduction={false} isOpenAPIEnabled={false} />
);

export const Primary = Template.bind({});
