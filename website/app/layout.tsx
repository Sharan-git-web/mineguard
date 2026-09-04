import type { Metadata } from 'next';
import './globals.css';
import { MainLayout } from '@/components/layout/MainLayout';

export const metadata: Metadata = {
  title: 'Smart Mine Governance — Regional Coal Command Workspace',
  description: 'Coal Mine Safety & Compliance Directorate Officer Command Portal',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="bg-surface text-on-surface antialiased overflow-x-hidden min-h-screen">
        <MainLayout>{children}</MainLayout>
      </body>
    </html>
  );
}
