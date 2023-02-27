import { themes } from '@storybook/theming';

export const decorators = [];

export const parameters = {
  actions: { argTypesRegex: "^on[A-Z].*" },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
  docs: {
    theme: themes.normal,
  },
  // https://storybook.js.org/addons/@etchteam/storybook-addon-status
  status: {
    statuses: {
      released: {
        background: '#0000ff',
        color: '#ffffff',
        description: 'This component is stable and released',
      },
    },
  },
}
