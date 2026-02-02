import useBaseUrl from '@docusaurus/useBaseUrl';
import React from 'react';

export default function Home(): JSX.Element {
  // Call useBaseUrl at the top level of your component
  const mainUrl = useBaseUrl('/docs/environment');

  React.useEffect(() => {
    window.location.href = mainUrl;
  }, [mainUrl]); // Add mainUrl as a dependency to useEffect

  return null;
}
