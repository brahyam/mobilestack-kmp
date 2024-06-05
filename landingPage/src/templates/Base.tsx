import { Meta } from '../layout/Meta';
import { AppConfig } from '../utils/AppConfig';
import { FAQ } from './FAQ';
import { Features } from './Features';
import { Footer } from './Footer';
import { Header } from './Header';
import { Hero } from './Hero';
import { Pricing } from './Pricing';
import { Problem } from './Problem';
import { Testimonials } from './Testimonials';

const Base = () => (
  <div className="text-gray-600 antialiased">
    <Meta title={AppConfig.title} description={AppConfig.description} />
    <Header />
    <Hero />
    <Problem />
    <Features />
    <Pricing />
    <FAQ />
    <Testimonials />
    <Footer />
  </div>
);

export { Base };
