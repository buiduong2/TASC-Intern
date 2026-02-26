interface SideBarItem {
  id: string
  label: string;
  icon: string;
  route?: string;
  children?: SideBarItem[];
}
