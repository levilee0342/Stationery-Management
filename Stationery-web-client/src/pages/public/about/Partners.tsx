import React from "react";

interface Partner {
  id: number;
  logoUrl: string;
  name: string;
}

interface PartnersProps {
  partners: Partner[];
}

const Partners: React.FC<PartnersProps> = ({ partners }) => {
  return (
    <section className="py-12 bg-gray-100">
      <div className="container mx-auto px-4">
        <h2 className="text-3xl text-blue-800 font-semibold text-center mb-8">Partners</h2>
        <div className="relative w-full overflow-hidden">
          {/* Wrapper cho hiệu ứng vòng tròn */}
          <div className="flex animate-infinite-marquee w-max">
            {partners.concat(partners).map((partner, index) => (
              <img
                key={index}
                src={partner.logoUrl}
                alt={partner.name}
                className="h-16 mx-4 object-contain"
              />
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};

export default Partners;
