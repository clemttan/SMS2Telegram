"""Render an illustrated UI preview; not an emulator or device screenshot."""
from PIL import Image, ImageDraw, ImageFont
from pathlib import Path
root=Path(__file__).parent
im=Image.new('RGB',(1120,1080),'#f0f3f8'); d=ImageDraw.Draw(im)
font='/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf'
bold='/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf'
def text(x,y,s,size=20,color='#eeeeee',strong=False):
 d.text((x,y),s,font=ImageFont.truetype(bold if strong else font,size),fill=color)
def rr(box,fill,r=10):d.rounded_rectangle(box,r,fill)
def screen(x):
 rr((x,95,x+510,958),'#121212',18)
 text(x+22,115,'12:30',16); text(x+411,115,'84%',16)
 d.rectangle((x,153,x+510,224),fill='#2962f5');text(x+20,172,'SMS2Telegram',27,strong=True)
 text(x+24,251,'Configure Telegram',23,strong=True)
 text(x+24,291,'Enter your bot token and chat IDs.',17,color='#bdbdbd')
 rr((x+24,337,x+486,423),'#303030',5)
 text(x+40,347,'Telegram Bot API Token',15,color='#bdbdbd')
 text(x+40,379,'••••••••••••••••••••••••',22)
 rr((x+24,451,x+486,586),'#303030',5)
 text(x+40,465,'Telegram Chat IDs',16,color='#82a7ff')
 text(x+40,500,'123456789',22);text(x+40,534,'987654321',22)
 d.line((x+24,585,x+486,585),fill='#82a7ff',width=2)
 text(x+40,605,'Enter one ID per line, or use commas.',16,color='#bdbdbd')
 text(x+40,634,'Every recipient must start your bot first.',16,color='#bdbdbd')
 text(x+34,688,'FIND CHAT IDS',20,color='#82a7ff',strong=True)
 rr((x+24,762,x+486,824),'#2962f5',5)
 text(x+48,783,'VALIDATE AND CONTINUE',21,strong=True)
 d.rounded_rectangle((x+178,932,x+332,938),3,fill='#bbbbbb')
text(30,24,'Multiple Telegram recipients',31,color='#192238',strong=True)
text(30,65,'Illustrated UI preview · fictional chats and IDs',17,color='#536079')
for x in (30,580):screen(x)
# Dialog over the first screen; second screen shows the resulting editable list.
shade=Image.new('RGBA',im.size,(0,0,0,0));sd=ImageDraw.Draw(shade)
sd.rounded_rectangle((30,95,540,958),18,fill=(0,0,0,155))
im=Image.alpha_composite(im.convert('RGBA'),shade);d=ImageDraw.Draw(im)
rr((54,384,516,714),'#424242',8)
text(78,410,'Select chats',25,strong=True)
for y,name,ident in [(481,'Alex (@example_alex)','123456789'),(573,'Sam (@example_sam)','987654321')]:
 d.rectangle((80,y+7,104,y+31),fill='#82a7ff')
 d.line([(85,y+19),(91,y+25),(100,y+12)],fill='#232323',width=3)
 text(122,y,name,18);text(122,y+29,'ID = '+ident,18,color='#dedede')
text(300,671,'CANCEL',18,color='#82a7ff',strong=True)
text(413,671,'APPLY',18,color='#82a7ff',strong=True)
text(48,983,'1. Select several chats',22,color='#192238',strong=True)
text(48,1019,'Tick or untick, then Apply. Cancel keeps edits.',17,color='#536079')
text(598,983,'2. Validate the selected IDs',22,color='#192238',strong=True)
text(598,1019,'Apply fills the field; validation saves settings.',17,color='#536079')
im.convert('RGB').save(root/'telegram-multiple-recipients.png',optimize=True)
