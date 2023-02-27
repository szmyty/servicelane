import type { StorybookConfig } from '@storybook/core-common';

const config: StorybookConfig = {
  stories: [
    "../src/**/*.stories.mdx",
    "../src/**/*.stories.@(js|jsx|ts|tsx)"
  ],
  staticDirs: [
    "./assets"
  ],
  addons: [
    "@storybook/addon-links",
    "@storybook/addon-essentials",
    "@storybook/addon-interactions",
    "@storybook/addon-storysource",
    "@storybook/mdx2-csf-loader",
    "@etchteam/storybook-addon-status"
  ],
  typescript: {
    check: false,
    checkOptions: {},
    reactDocgen: "react-docgen-typescript",
    reactDocgenTypescriptOptions: {
      shouldExtractLiteralValuesFromEnum: true,
      propFilter: (prop) => (prop.parent ? !/node_modules/.test(prop.parent.fileName) : true),
    },
  },
  framework: "@storybook/react",
  core: {
    builder: "@storybook/builder-webpack5",
    disableTelemetry: true,
    enableCrashReports: false
  },
  features: {
    storyStoreV7: true,
    buildStoriesJson: true,
    emotionAlias: false,
    babelModeV7: false,
    postcss: false,
    modernInlineRender: false,
    previewMdx2: true,
    interactionsDebugger: false
  },
  logLevel: "debug",
}

module.exports = config;
