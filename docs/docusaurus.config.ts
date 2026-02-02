import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const isGitHubPages = process.env.GITHUB_ACTIONS === 'true';

const config: Config = {

  markdown: {
    mermaid: true,
  },
  themes: ['@docusaurus/theme-mermaid'],

  title: 'MobileStack - Docs',
  tagline: 'Ship mobile apps fast',
  favicon: 'img/favicon.ico',

  // Prod URL + baseUrl.
  // - GitHub Pages: https://brahyam.github.io/mobilestack-kmp/
  // - Custom domain: https://docs.getmobilestack.com/
  url: isGitHubPages ? 'https://brahyam.github.io' : 'https://docs.getmobilestack.com',
  baseUrl: isGitHubPages ? '/mobilestack-kmp/' : '/',

  // GitHub pages deployment config.
  organizationName: 'brahyam',
  projectName: 'mobilestack-kmp',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // editUrl:
          //   'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // editUrl:
          //   'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    image: 'img/social-card.webp',
    navbar: {
      title: 'MobileStack',
      logo: {
        alt: 'MobileStack',
        src: 'img/logo.png',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'mainSidebar',
          position: 'left',
          label: 'Docs',
        },
        // {to: '/blog', label: 'Blog', position: 'left'},
        {
          href: 'https://getmobilestack.com',
          label: 'Get MobileStack',
          position: 'right',
        },
        {
          href: 'https://github.com/brahyam/mobilestack-kmp',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Environment',
              to: '/docs/environment',
            },
            {
              label: 'Setup',
              to: '/docs/category/setup',
            },
            {
              label: 'Features',
              to: '/docs/category/features',
            }
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Discord',
              href: 'https://discord.com/channels/1232698243884908644/1232698243884908649',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/brahyam/mobilestack-kmp',
            }
          ],
        },
        {
          title: 'More',
          items: [
            // {
            //   label: 'Blog',
            //   to: '/blog',
            // },
            {
              label: 'MobileStack',
              href: 'https://getmobilestack.com',
            },
            {
              label: 'Zenith Apps',
              href: 'https://zenithapps.com',
            },
            {
              label: 'Everblue App',
              href: 'https://everblue.life',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} MobileStack.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
